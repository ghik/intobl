/**
 * Copyright (C) 2006 - 2012
 *   Pawel Kedzior
 *   Tomasz Kmiecik
 *   Kamil Pietak
 *   Krzysztof Sikora
 *   Adam Wos
 *   Lukasz Faber
 *   Daniel Krzywicki
 *   and other students of AGH University of Science and Technology.
 *
 * This file is part of AgE.
 *
 * AgE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AgE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AgE.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created: 2012-08-21
 * $Id: DefaultLifecycleManager.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.lifecycle;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.component.DefaultComponentAddress;
import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddressSupplier;
import org.jage.communication.CommunicationService;
import org.jage.communication.IncomingMessageListener;
import org.jage.communication.message.IMessage;
import org.jage.lifecycle.LifecycleHeader.LifecycleCommand;
import org.jage.platform.argument.InvalidRuntimeArgumentsException;
import org.jage.platform.argument.RuntimeArgumentsService;
import org.jage.platform.component.IStatefulComponent;
import org.jage.platform.component.definition.ConfigurationException;
import org.jage.platform.component.definition.IComponentDefinition;
import org.jage.platform.component.exception.ComponentException;
import org.jage.platform.component.provider.IMutableComponentInstanceProvider;
import org.jage.platform.component.provider.IMutableComponentInstanceProviderAware;
import org.jage.platform.config.loader.IConfigurationLoader;
import org.jage.platform.config.xml.ConfigurationLoader;
import org.jage.platform.fsm.CallableWithParameters;
import org.jage.platform.fsm.StateMachineService;
import org.jage.platform.fsm.StateMachineServiceBuilder;
import org.jage.services.core.ConfigurationService;
import org.jage.services.core.CoreComponent;
import org.jage.services.core.ICoreComponentListener;
import org.jage.services.core.InteractiveController;
import org.jage.services.core.LifecycleManager;

import static org.jage.communication.message.Messages.getSenderAddress;
import static org.jage.lifecycle.LifecycleMessages.createExit;
import static org.jage.lifecycle.LifecycleMessages.createHasInteractiveControllerNotification;
import static org.jage.lifecycle.LifecycleMessages.createNoInteractiveControllerNotification;
import static org.jage.lifecycle.LifecycleMessages.createStart;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A default lifecycle manager for generic AgE nodes.
 *
 * @author AGH AgE Team
 */
public class DefaultLifecycleManager implements ICoreComponentListener, IMutableComponentInstanceProviderAware,
        IncomingMessageListener, LifecycleManager, IStatefulComponent {

	/**
	 * The name of the node configuration file parameter.
	 */
	public static final String NODE_CONFIGURATION_FILE_OPTION = "age.node.conf";

	private static final String COMPONENT_NAME = LifecycleManager.class.getSimpleName();

	private static final Logger log = LoggerFactory.getLogger(DefaultLifecycleManager.class);

	private final StateMachineService<State, Event> service;

	private IMutableComponentInstanceProvider instanceProvider;

	private CoreComponent coreComponent;

	@Inject
	private RuntimeArgumentsService argumentsService;

	private CommunicationService communicationService;

	@Nullable
	private ComponentAddress componentAddress;

	private long interactiveControllerNotificationsCount = 0;

	private boolean interactiveControllerExists = false;

	/**
	 * Constructs a new lifecycle manager.
	 */
	public DefaultLifecycleManager() {
		// XXX: Explicit types because of javac bug
		// http://stackoverflow.com/questions/2858799/generics-compiles-and-runs-in-eclipse-but-doesnt-compile-in
		// -javac
		// May be removed after dropping Java 6
		final StateMachineServiceBuilder<State, Event> builder = StateMachineServiceBuilder.<State, Event> create();

		//@formatter:off
		builder
			.states(State.class).events(Event.class)
			.startWith(State.OFFLINE)
			.terminateIn(State.TERMINATED)

			.in(State.OFFLINE)
				.on(Event.INITIALIZE).execute(new InitializationAction()).goTo(State.INITIALIZED).commit()
			.in(State.INITIALIZED)
				.on(Event.CONFIGURE).execute(new ConfigurationAction()).goTo(State.CONFIGURED).commit()
			.in(State.CONFIGURED)
				.on(Event.START_COMMAND).execute(new StartAction()).goTo(State.RUNNING).commit()
			.in(State.RUNNING)
				.on(Event.CORE_STARTING).goTo(State.RUNNING).and()
				.on(Event.PAUSE).execute(new PauseAction()).goTo(State.PAUSED).and()
				.on(Event.STOP_COMMAND).execute(new StopAction()).goTo(State.STOPPED).commit()
			.in(State.PAUSED)
				.on(Event.RESUME).execute(new ResumeAction()).goTo(State.RUNNING).commit()
			.in(State.STOPPED)
				.on(Event.CORE_STOPPED).execute(new CoreStoppedAction()).goTo(State.STOPPED).and()
				.on(Event.CLEAR).execute(new ClearAction()).goTo(State.INITIALIZED).commit()

			.inAnyState()
				.on(Event.EXIT).execute(new ExitAction()).goTo(State.TERMINATED).and()
				.on(Event.ERROR).execute(new ErrorAction()).goTo(State.FAILED).commit()

			.ifFailed()
				.fire(Event.ERROR)

			.shutdownWhenTerminated();

		service = builder.build();
		//@formatter:on

		// Register shutdown hook, so we will be able to do a clean shutdown
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
	}

	/**
	 * States of this lifecycle manager (in other words - states of the node).
	 *
	 * @author AGH AgE Team
	 */
	static enum State {
		/**
		 * Initial state of the node.
		 */
		OFFLINE,
		/**
		 * Node has been initialized.
		 */
		INITIALIZED,
		/**
		 * Computation was loaded and initialized.
		 */
		CONFIGURED,
		/**
		 * Computation is running.
		 */
		RUNNING,
		/**
		 * Computation is paused.
		 */
		PAUSED,
		/**
		 * Computation has been stopped.
		 */
		STOPPED,
		/**
		 * Node has failed.
		 */
		FAILED,
		/**
		 * Node has terminated (terminal state).
		 */
		TERMINATED;
	}

	/**
	 * Events that can occur in the node.
	 *
	 * @author AGH AgE Team
	 */
	static enum Event {
		/**
		 * Sent by the bootstrapper.
		 */
		INITIALIZE,
		/**
		 * Sent by the configuration service when configuration is available.
		 */
		CONFIGURE,
		/**
		 * Starts the computation.
		 */
		START_COMMAND,
		/**
		 * Notifies that the core component is starting.
		 */
		CORE_STARTING,
		/**
		 * Pauses the computation.
		 */
		PAUSE,
		/**
		 * Resumes the computation.
		 */
		RESUME,
		/**
		 * Stops the computation completely.
		 */
		STOP_COMMAND,
		/**
		 * Notifies that the core component has stopped.
		 */
		CORE_STOPPED,
		/**
		 * Clears the node from the computation configuration.
		 */
		CLEAR,
		/**
		 * Indicates that an error occurred.
		 */
		ERROR,
		/**
		 * Terminates the node.
		 */
		EXIT;
	}

	// Interface methods that translate environment changes to events

	@Override
	public void setMutableComponentInstanceProvider(final IMutableComponentInstanceProvider provider) {
		instanceProvider = provider;
	}

	@Override
	public void deliver(final IMessage<? extends ComponentAddress, ? extends Serializable> message) {
		if (!(message instanceof LifecycleMessage)) {
			// We ignore non-lifecycle messages but notify about them.
			log.error("{} sent unreadable message to me. Offending message is {}.", getSenderAddress(message), message);
			return;
		}

		final LifecycleMessage lifecycleMessage = (LifecycleMessage)message;

		final LifecycleCommand command = lifecycleMessage.getCommand();
		switch (command) {
			case FAIL:
				break;
			case NOTIFY:
				handleNotifyCommand(lifecycleMessage.getPayload());
				break;
			case PAUSE:
				service.fire(Event.PAUSE);
				break;
			case START:
				service.fire(Event.START_COMMAND);
				break;
			case STOP:
				service.fire(Event.STOP_COMMAND);
				break;
			case EXIT:
				service.fire(Event.EXIT);
				break;
			default:
				break;
		}
	}

	private void handleNotifyCommand(final Serializable payload) {
		final Map<String, Object> data = (Map<String, Object>)checkNotNull(payload);

		for (final Entry<String, Object> entry : data.entrySet()) {
			if ("interactiveController".equals(entry.getKey())) {
				interactiveControllerNotificationsCount++;
				final Boolean hasIC = (Boolean)entry.getValue();
				if (hasIC) {
					log.info("There is an interactive controller in the environment.");
					interactiveControllerExists = true;
				}
			}
		}

		if (interactiveControllerNotificationsCount == communicationService.getNodesCount()) {
			log.debug("Received all expected notifications about the interactive controller.");

			if (interactiveControllerExists) {
				log.info("Running interactively.");
			} else {
				log.info("Batch run. Will try to start the computation.");
				if (communicationService.isLocalNodeMaster()) {
					communicationService.send(createStart(componentAddress));
				}
			}
		}
	}

	@Override
	public void init() {
		service.fire(Event.INITIALIZE);
	}

	@Override
	public boolean finish() {
		return true;
	}

	@Override
	public void onCoreComponentStarting(final CoreComponent core) {
		service.fire(Event.CORE_STARTING);
	}

	@Override
	public void onCoreComponentStopped(final CoreComponent core) {
		service.fire(Event.CORE_STOPPED);
	}

	@Override
	public void onNewComputationConfiguration(final boolean alreadyInjected,
	        final List<IComponentDefinition> configuration) {
		service.fire(Event.CONFIGURE, alreadyInjected, configuration);
	}

	@Override
	public void onStopConditionFulfilled() {
		log.debug("Stop condition fulfilled.");
		service.fire(Event.STOP_COMMAND);
	}

	@Override
	public String toString() {
		return toStringHelper(this).addValue(service).toString();
	}

	@Override
	public void register(final Object listener) {
		service.register(listener);
	}

	@Override
	public void unregister(final Object listener) {
		service.unregister(listener);
	}

	// Implementations of actions

	private class InitializationAction implements Runnable {
		@Override
		public void run() {
			log.debug("Initializing LifecycleManager.");
			instanceProvider.addComponent(ConfigurationLoader.class);

			final String configFilePath = argumentsService.getCustomOption(NODE_CONFIGURATION_FILE_OPTION);

			if (configFilePath == null) {
				throw new InvalidRuntimeArgumentsException(String.format(
				        "The node config file name parameter is missing. Specify the correct path "
				                + "to the configuration file using -D%s option", NODE_CONFIGURATION_FILE_OPTION));
			}

			try {
				final Collection<IComponentDefinition> nodeComponents = instanceProvider.getInstance(
				        IConfigurationLoader.class).loadConfiguration(configFilePath);

				for (final IComponentDefinition def : nodeComponents) {
					instanceProvider.addComponent(def);
				}
				instanceProvider.verify();
			} catch (final ComponentException e) {
				throw new LifecycleException("Cannot perform components initialization.", e);
			} catch (final ConfigurationException e) {
				throw new LifecycleException("Cannot perform components initialization.", e);
			}

			instanceProvider.getInstances(IStatefulComponent.class);

			communicationService = instanceProvider.getInstance(CommunicationService.class);
			final NodeAddressSupplier nodeAddressProvider = instanceProvider
			        .getInstance(NodeAddressSupplier.class);

			if (communicationService == null) {
				throw new LifecycleException("There is no CommunicationService in the platform.");
			} else if (nodeAddressProvider == null) {
				throw new LifecycleException("CommunicationService is available. We need AgENodeAddressProvider too!");
			} else {
				componentAddress = new DefaultComponentAddress(COMPONENT_NAME, nodeAddressProvider.get());
				communicationService.addMessageListener(COMPONENT_NAME, DefaultLifecycleManager.this);
				log.debug("My address is {}.", componentAddress);
			}

			coreComponent = instanceProvider.getInstance(CoreComponent.class);
			if (coreComponent == null) {
				throw new LifecycleException("Core component (CoreComponent) is missing. Cannot run the computation.");
			}
			coreComponent.registerListener(DefaultLifecycleManager.this);

			final ConfigurationService configurationService = instanceProvider.getInstance(ConfigurationService.class);
			try {
				configurationService.obtainConfiguration();
			} catch (final ConfigurationException e) {
				throw new LifecycleException("Cannot initialize the configuration service.", e);
			}
			log.debug("Node has finished initialization.");
		}
	}

	private class ConfigurationAction implements CallableWithParameters<Object[]> {
		@SuppressWarnings("unchecked")
		@Override
		public void call(final Object[] parameters) {
			log.debug("Configuring the node.");
			final boolean configurationAlreadyInjected = (Boolean)parameters[0];
			final List<IComponentDefinition> currentConfiguration = (List<IComponentDefinition>)parameters[1];

			if (!configurationAlreadyInjected) {
				if (currentConfiguration == null) {
					throw new LifecycleException("Someone started the operation without providing the configuration.");
				}
				try {
					coreComponent.computationConfigurationUpdated(currentConfiguration);
				} catch (final ComponentException e) {
					throw new LifecycleException("Core component was not able to configure the computation.", e);
				}
			}

			log.debug("Node is configured.");

			final InteractiveController interactiveController = instanceProvider
			        .getInstance(InteractiveController.class);
			if (interactiveController == null) {
				log.debug("No interactive controller in this node.");
				communicationService.send(createNoInteractiveControllerNotification(componentAddress));
			} else {
				log.debug("I have an interactive controller.");
				interactiveControllerExists = true;
				communicationService.send(createHasInteractiveControllerNotification(componentAddress));
			}
		}
	}

	private class StartAction implements Runnable {
		@Override
		public void run() {
			log.info("Computation is starting.");

			final String barrierName = COMPONENT_NAME + "-START_COMMAND";
			log.debug("Global synchronisation on {}.", barrierName);
			try {
				communicationService.barrier(barrierName);
			} catch (final InterruptedException e) {
				log.error("Interrupted when on barrier. Aborting.", e);
				throw new LifecycleException("Interrupted during barrier await on START_COMMAND.", e);
			}

			try {
				coreComponent.start();
			} catch (final ComponentException e) {
				throw new LifecycleException("The core component could not start.", e);
			}
		}
	}

	private class PauseAction implements Runnable {
		@Override
		public void run() {
			log.info("Computation is pausing.");

			coreComponent.pause();
		}
	}

	private class ResumeAction implements Runnable {
		@Override
		public void run() {
			log.info("Computation is resuming.");

			coreComponent.resume();
		}
	}

	private class StopAction implements Runnable {
		@Override
		public void run() {
			log.info("Computation is stopping.");

			final String barrierName = COMPONENT_NAME + "-STOP_COMMAND";
			log.debug("Global synchronisation on {}.", barrierName);
			try {
				communicationService.barrier(barrierName);
			} catch (final InterruptedException e) {
				log.error("Interrupted when on barrier. Aborting.", e);
				throw new LifecycleException("Interrupted during barrier await on STOP_COMMAND.", e);
			}

			coreComponent.stop();
		}
	}

	private class CoreStoppedAction implements Runnable {
		@Override
		public void run() {
			log.debug("CoreComponent has stopped.");

			if (!interactiveControllerExists) {
				log.debug("Batch run. Will try to exit.");
				if (communicationService.isLocalNodeMaster()) {
					communicationService.send(createExit(componentAddress));
				}
			}
		}
	}

	private class ClearAction implements Runnable {
		@Override
		public void run() {
			log.info("Computation configuration is being removed.");

			coreComponent.resume();
		}
	}

	private class ExitAction implements Runnable {
		@Override
		public void run() {
			log.debug("Node is terminating.");

			try {
				final Collection<IStatefulComponent> statefulComponents = instanceProvider
				        .getInstances(IStatefulComponent.class);
				if (statefulComponents != null) {
					for (final IStatefulComponent statefulComponent : statefulComponents) {
						statefulComponent.finish();
					}
				}
			} catch (final ComponentException e) {
				log.error("Exception during the teardown.", e);
			}
			log.info("Node terminated.");

			final Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();

			for (final Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
				final Thread thread = entry.getKey();
				if (thread != Thread.currentThread() && thread.isAlive() && !thread.isDaemon()) {
					log.debug("{} has not been shutdown properly.", entry.getKey());
					for (final StackTraceElement e : entry.getValue()) {
						log.debug("\t{}", e);
					}
				}
			}
		}
	}

	private class ErrorAction implements CallableWithParameters<Throwable> {
		@Override
		public void call(final Throwable parameter) {
			log.error("Node failed with exception.", parameter);

			log.info("If you are running the node from the console, press Ctrl-C to exit.");
		}
	}

	private class ShutdownHook extends Thread {
		@Override
		public void run() {
			log.debug("Shutdown hook called.");
			if (!service.terminated() && !service.isTerminating()) {
				service.fire(Event.EXIT);
				try {
					Thread.sleep(1000); // Simple wait to let other threads finish properly.
				} catch (final InterruptedException ignored) {
					// Ignore
				}
			}
		}
	}

}
