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
 * Created: 2008-10-07
 * $Id: DefaultWorkplaceManager.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.workplace.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.GuardedBy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.agent.AgentAddress;
import org.jage.address.component.DefaultComponentAddress;
import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddressSupplier;
import org.jage.address.selector.IAddressSelector;
import org.jage.agent.IAgent;
import org.jage.communication.CommunicationService;
import org.jage.communication.IncomingMessageListener;
import org.jage.communication.message.IMessage;
import org.jage.platform.component.definition.IComponentDefinition;
import org.jage.platform.component.exception.ComponentException;
import org.jage.platform.component.pico.IPicoComponentInstanceProvider;
import org.jage.platform.component.pico.PicoComponentInstanceProvider;
import org.jage.platform.component.provider.IMutableComponentInstanceProvider;
import org.jage.platform.component.provider.IMutableComponentInstanceProviderAware;
import org.jage.platform.config.ConfigurationChangeListener;
import org.jage.property.ClassPropertyContainer;
import org.jage.property.PropertyGetter;
import org.jage.property.PropertySetter;
import org.jage.query.AgentEnvironmentQuery;
import org.jage.services.core.ICoreComponentListener;
import org.jage.services.core.LifecycleManager;
import org.jage.util.Locks;
import org.jage.workplace.IStopCondition;
import org.jage.workplace.Workplace;
import org.jage.workplace.IWorkplaceEnvironment;
import org.jage.workplace.WorkplaceException;

import static org.jage.communication.message.Messages.newBroadcastMessage;

import com.google.common.collect.ImmutableList;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Default implementation of {@link WorkplaceManager}.
 * <p>
 *
 * This implementation synchronises in two points when run in a distributed environment:
 * <ol>
 * <li>just before starting the computation,
 * <li>just before stopping the computation.
 * </ol>
 *
 * @author AGH AgE Team
 */
@ParametersAreNonnullByDefault
public class DefaultWorkplaceManager extends ClassPropertyContainer implements WorkplaceManager, IWorkplaceEnvironment,
        IMutableComponentInstanceProviderAware, ConfigurationChangeListener, IncomingMessageListener {

	private static final Logger log = LoggerFactory.getLogger(DefaultWorkplaceManager.class);

	private static final String COMPONENT_NAME = DefaultWorkplaceManager.class.getSimpleName();

	@GuardedBy("itself")
	@Nonnull
	private final List<ICoreComponentListener> listeners = newLinkedList();

	@GuardedBy("workplacesLock")
	@Nonnull
	private final Map<AgentAddress, Workplace<IAgent>> workplaces = newHashMap();

	@Nonnull
	private final List<Workplace<IAgent>> activeWorkplaces = newLinkedList();

	@Nonnull
	private final ReadWriteLock workplacesLock = new ReentrantReadWriteLock(true);

	protected PicoComponentInstanceProvider instanceProvider;

	private List<Workplace<IAgent>> injectedWorkplaces;

	@Inject
	private CommunicationService communicationService;

	@Inject
	private NodeAddressSupplier nodeAddressProvider;

	private ComponentAddress address;

	@Inject
	private LifecycleManager lifecycleManager;

	private IPicoComponentInstanceProvider childContainer;

	// Lifecycle methods

	@Override
	public void init() throws ComponentException {
		address = new DefaultComponentAddress(COMPONENT_NAME, nodeAddressProvider.get());
		communicationService.addMessageListener(COMPONENT_NAME, this);
		log.debug("My address is {}.", address);
	}

	@Override
	public void start() throws ComponentException {
		checkState(injectedWorkplaces != null, "There are no workplaces configured.");
		initializeWorkplaces();

		instanceProvider.getInstance(IStopCondition.class);
		if (childContainer != null) {
			childContainer.getInstance(IStopCondition.class);
		}

		// notify all listeners
		synchronized (listeners) {
			for (final ICoreComponentListener listener : listeners) {
				listener.onCoreComponentStarting(this);
			}
		}

		// start all workplaces
		withReadLockThrowing(new Callable<Object>() {
			@Override
			public Object call() throws ComponentException {
				checkState(!workplaces.isEmpty(), "There is no workplace to run.");

				for (final Workplace<IAgent> workplace : workplaces.values()) {
					workplace.start();
					activeWorkplaces.add(workplace);
					log.info("Workplace {} started.", workplace);
				}
				return null;
			}
		}, ComponentException.class);
	}

	@Override
	public void pause() {
		log.debug("Workplace manager is pausing computation.");
		withReadLock(new Runnable() {
			@Override
			public void run() {
				for (final Workplace<IAgent> workplace : workplaces.values()) {
					synchronized (workplace) {
						if (workplace.isRunning()) {
							workplace.pause();
						} else {
							log.warn("Trying to pause not running workplace: {}.", workplace);
						}
					}
				}
			}
		});
	}

	@Override
	public void resume() {
		log.debug("Workplace manager is resuming computation.");
		withReadLock(new Runnable() {
			@Override
			public void run() {
				for (final Workplace<IAgent> workplace : workplaces.values()) {
					synchronized (workplace) {
						if (workplace.isPaused()) {
							workplace.resume();
						} else {
							log.warn("Trying to resume not paused workplace: {}.", workplace);
						}
					}
				}
			}
		});
	}

	@Override
	public void stop() {
		log.debug("Workplace manager is stopping.");
		withReadLock(new Runnable() {
			@Override
			public void run() {
				for (final Workplace<IAgent> workplace : workplaces.values()) {
					synchronized (workplace) {
						if (workplace.isRunning() || workplace.isPaused()) {
							workplace.stop();
						} else {
							log.warn("Trying to stop not running workplace: {}.", workplace);
						}
					}
				}
			}
		});
	}

	@Override
	public boolean finish() throws ComponentException {
		withReadLockThrowing(new Callable<Object>() {
			@Override
			public Object call() throws ComponentException {
				for (final Workplace<IAgent> workplace : workplaces.values()) {
					synchronized (workplace) {
						if (workplace.isStopped()) {
							workplace.finish();
							log.info("Workplace {} finished", workplace);
						} else {
							log.error("Cannot finish still running workplace: {}.", workplace);
						}
					}
				}
				return null;
			}
		}, ComponentException.class);
		return true;
	}

	/**
	 * Indicates if the workplace manager is active, i.e. it contains at least on active workplace.
	 *
	 * @return true if the manager is active
	 */
	public boolean isActive() {
		return !activeWorkplaces.isEmpty();
	}

	// Core component listeners

	@Override
	public final void registerListener(final ICoreComponentListener listener) {
		checkNotNull(listener);

		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
				log.debug("Registered listener {} to DefaultWorkplaceManager.", listener);
			}
		}
	}

	@Override
	public final void unregisterListener(final ICoreComponentListener listener) {
		checkNotNull(listener);

		synchronized (listeners) {
			if (listeners.contains(listener)) {
				listeners.remove(listener);
				log.debug("Unregistered listener {} from DefaultWorkplaceManager.", listener);
			}
		}
	}

	@Override
	public void setMutableComponentInstanceProvider(final IMutableComponentInstanceProvider provider) {
		checkNotNull(provider);
		checkArgument(provider instanceof PicoComponentInstanceProvider,
		        "This class requires more advanced view of the provider.");
		instanceProvider = ((PicoComponentInstanceProvider)provider);
	}

	@Override
	public void onWorkplaceStop(final Workplace<? extends IAgent> workplace) {
		log.debug("Get stopped notification from {}", workplace.getAddress());
		if (activeWorkplaces.contains(workplace)) {
			activeWorkplaces.remove(workplace);
		} else {
			log.error("Received event notify stopped from workplace {} which is already stopped",
			        workplace.getAddress());
		}

		if (activeWorkplaces.isEmpty()) {
			final List<ICoreComponentListener> listenersCopy;
			synchronized (listeners) {
				listenersCopy = ImmutableList.copyOf(listeners);
			}
			for (final ICoreComponentListener listener : listenersCopy) {
				listener.onCoreComponentStopped(this);
			}
		}
	}

	/* Workplace management methods */

	/**
	 * Attaches a given workplace to this manager.
	 *
	 * @param workplace
	 *            workplace to attache
	 * @throws WorkplaceException
	 *             when a given workplace is already attached to this manager or workplace's address exists already in
	 *             the manager
	 */
	public void addWorkplace(final Workplace<? extends IAgent> workplace) {
		withWriteLock(new Runnable() {
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				final AgentAddress agentAddress = workplace.getAddress();
				if (!workplaces.containsKey(agentAddress)) {
					workplaces.put(agentAddress, (Workplace<IAgent>)workplace);
					if (workplace.isRunning() && !activeWorkplaces.contains(workplace)) {
						workplace.setWorkplaceEnvironment(DefaultWorkplaceManager.this);
						activeWorkplaces.add((Workplace<IAgent>)workplace);
					}
					log.info("Workplace added: {}", workplace.getAddress());
				} else {
					throw new WorkplaceException(String.format("%s already exists in the manager.", agentAddress));
				}
			}
		});
	}

	@Override
	public Workplace<IAgent> getWorkplace(final AgentAddress workplaceAddress) {
		return withReadLockAndRuntimeExceptions(new Callable<Workplace<IAgent>>() {
			@Override
			public Workplace<IAgent> call() {
				return workplaces.get(workplaceAddress);
			}
		});
	}

	@Override
	@PropertyGetter(propertyName = "workplaces")
	public List<Workplace<IAgent>> getWorkplaces() {
		return withReadLockAndRuntimeExceptions(new Callable<List<Workplace<IAgent>>>() {
			@Override
			public List<Workplace<IAgent>> call() {
				return ImmutableList.copyOf(workplaces.values());
			}
		});
	}

	/**
	 * Sets the initial workplaces list.
	 *
	 * @param workplaces
	 *            the workplaces to put into this manager.
	 */
	@PropertySetter(propertyName = "workplaces")
	public void setWorkplaces(final List<Workplace<IAgent>> workplaces) {
		injectedWorkplaces = checkNotNull(workplaces);
	}

	private void initializeWorkplaces() throws ComponentException {
		if (injectedWorkplaces == null || injectedWorkplaces.isEmpty()) {
			throw new ComponentException("There is no workplace defined. Cannot run the computation.");
		}

		log.info("Created {} workplace(s).", injectedWorkplaces.size());

		withWriteLockThrowing(new Callable<Object>() {
			@Override
			public Object call() throws ComponentException {
				for (final Workplace<IAgent> workplace : injectedWorkplaces) {
					final AgentAddress agentAddress = workplace.getAddress();

					// We call setWorkplaceEnvironment() relying on the fact, that all agents and workplaces were
					// initialized before the workplace manager by the container.
					try {
						workplaces.put(agentAddress, workplace);
						workplace.setWorkplaceEnvironment(DefaultWorkplaceManager.this);
						log.info("Workplace {} initialized.", agentAddress);
					} catch (final WorkplaceException e) {
						workplaces.remove(agentAddress); // Do not leave not configured workplace in the manager
						throw new ComponentException(String.format("Cannot set workplace environment for: %s.",
						        agentAddress), e);
					}
				}
				return null;
			}
		}, ComponentException.class);
	}

	/* Workplace environment methods. */

	@SuppressWarnings("unchecked")
	@Override
	public <E extends IAgent, T> Collection<T> queryWorkplaces(final AgentEnvironmentQuery<E, T> query) {
		return query.execute((Collection<E>)getWorkplaces());
	}

	@Override
	public void sendMessage(final IMessage<AgentAddress, ?> message) {
		log.debug("Sending message {}.", message);
		// FIXME: Before this message will be send, the selector is initialised! We need to clone the message!
		broadcastToAllManagers(message);
		deliverMessageToWorkplace(message);
	}

	private void deliverMessageToWorkplace(final IMessage<AgentAddress, ?> message) {
		withReadLock(new Runnable() {
			@Override
			public void run() {
				final IAddressSelector<AgentAddress> receiverSelector = message.getHeader().getReceiverSelector();
				receiverSelector.initialize(getListOfWorkplaceAddresses(), getListOfWorkplaceAddresses());
				for (final AgentAddress agentAddress : receiverSelector.addresses()) {
					log.debug("Delivering to {}.", agentAddress);
					getWorkplace(agentAddress).deliverMessage(message);
				}
			}
		});
	}

	protected List<AgentAddress> getListOfWorkplaceAddresses() {
		final List<AgentAddress> addresses = newArrayListWithCapacity(getWorkplaces().size());

		for (final IAgent workplace : getWorkplaces()) {
			addresses.add(workplace.getAddress());
		}
		return addresses;
	}

	/* Distribution-awareness methods */

	@SuppressWarnings("unchecked")
	@Override
	public void computationConfigurationUpdated(final Collection<IComponentDefinition> componentDefinitions)
	        throws ComponentException {
		checkNotNull(componentDefinitions);

		if (injectedWorkplaces != null) {
			throw new ComponentException("The core component is already configured.");
		}

		childContainer = instanceProvider.makeChildContainer();
		for (final IComponentDefinition def : componentDefinitions) {
			childContainer.addComponent(def);
		}
		childContainer.verify();

		injectedWorkplaces = (List)newArrayList(childContainer.getInstances(Workplace.class));

		log.info("Loaded workplaces: {}.", injectedWorkplaces);

		// Initialise a stop condition
		// childInstanceProvider.getInstance(IStopCondition.class);

		// initializeWorkplaces();
	}

	@Override
    public void teardownConfiguration() {
		checkState(childContainer != null, "The situation without separate computation configuration is not supported.");
		instanceProvider.removeChildContainer(childContainer);
		injectedWorkplaces = null;
	}

	@Override
	public void deliver(final IMessage<? extends ComponentAddress, ? extends Serializable> message) {
		final IMessage<AgentAddress, Serializable> agentMessage = (IMessage<AgentAddress, Serializable>)message
		        .getPayload();
		deliverMessageToWorkplace(agentMessage);
	}

	/**
	 * Broadcasts a payload to all managers in the distributed environment.
	 *
	 * @param payload
	 *            a payload to send.
	 */
	protected void broadcastToAllManagers(final Serializable payload) {
		log.debug("Broadcasting {} from {}.", payload, address);
		communicationService.send(newBroadcastMessage(address, payload));
	}

	/* Lock utilities. */

	/**
	 * Executes the provided {@link Runnable} with locked workplaces' lock. The lock is locked before execution and
	 * guaranteed to be unlocked after finishing the call.
	 *
	 * <p>
	 * This method locks on a <em>read</em> lock of the {@link ReadWriteLock}.
	 *
	 * @param action
	 *            the runnable to execute.
	 *
	 * @see #withReadLockAndRuntimeExceptions(Callable) for a rationale of behaviour for exceptions.
	 * @see Locks#withReadLock(ReadWriteLock, Runnable)
	 */
	protected final void withReadLock(final Runnable action) {
		withReadLockAndRuntimeExceptions(Executors.callable(action));
	}

	/**
	 * Executes the provided {@link Callable} with locked workplaces' lock. The lock is locked before execution and
	 * guaranteed to be unlocked after finishing the call.
	 *
	 * <p>
	 * This method locks on a <em>read</em> lock of the {@link ReadWriteLock}. Interrupted exception is logged and the
	 * interrupted status is set. All other exceptions are logged and rethrown as runtime exceptions. (The rationale
	 * behind this is that this method should be used only for locking all operations on workplaces' collection and it
	 * does not throw exceptions.)
	 *
	 * @param action
	 *            the callable to execute.
	 * @return an object returned by the callable.
	 * @param <V>
	 *            a type of the value returned by the callable.
	 *
	 * @see Locks#withReadLock(ReadWriteLock, Callable)
	 */
	protected final <V> V withReadLockAndRuntimeExceptions(final Callable<V> action) {
		assert (action != null);
		return Locks.withReadLockAndRuntimeExceptions(workplacesLock, action);
	}

	/**
	 * Executes the provided {@link Runnable} with locked workplaces' lock. The lock is locked before execution and
	 * guaranteed to be unlocked after finishing the call.
	 *
	 * <p>
	 * This method locks on a <em>write</em> lock of the {@link ReadWriteLock}.
	 *
	 * @param action
	 *            the runnable to execute.
	 *
	 * @see #withReadLockAndRuntimeExceptions(Callable) for a rationale of behaviour for exceptions.
	 * @see Locks#withWriteLock(ReadWriteLock, Runnable)
	 */
	protected final void withWriteLock(final Runnable action) {
		withWriteLockAndRuntimeExceptions(Executors.callable(action));
	}

	/**
	 * Executes the provided {@link Callable} with locked workplaces' lock. The lock is locked before execution and
	 * guaranteed to be unlocked after finishing the call.
	 *
	 * <p>
	 * This method locks on a <em>write</em> lock of the {@link ReadWriteLock}.
	 *
	 * @param action
	 *            the callable to execute.
	 * @return an object returned by the callable.
	 * @param <V>
	 *            a type of the value returned by the callable.
	 *
	 * @see #withReadLockAndRuntimeExceptions(Callable) for a rationale of behaviour for exceptions.
	 * @see Locks#withWriteLock(ReadWriteLock, Callable)
	 */
	protected final <V> V withWriteLockAndRuntimeExceptions(final Callable<V> action) {
		assert (action != null);
		return Locks.withWriteLockAndRuntimeExceptions(workplacesLock, action);
	}

	protected final <V, E extends Exception> V withWriteLockThrowing(final Callable<V> action,
	        final Class<E> exceptionClass) throws E {
		assert (action != null && exceptionClass != null);
		return Locks.withWriteLockThrowing(workplacesLock, action, exceptionClass);
	}

	protected final <V, E extends Exception> V withReadLockThrowing(final Callable<V> action,
	        final Class<E> exceptionClass) throws E {
		assert (action != null && exceptionClass != null);
		return Locks.withReadLockThrowing(workplacesLock, action, exceptionClass);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("address", address).toString();
	}
}
