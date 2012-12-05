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
 * $Id: DefaultConfigurationService.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.lifecycle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.component.DefaultComponentAddress;
import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddressSupplier;
import org.jage.address.selector.BroadcastSelector;
import org.jage.communication.CommunicationService;
import org.jage.communication.message.Header;
import org.jage.communication.message.IHeader;
import org.jage.communication.message.IMessage;
import org.jage.communication.message.Message;
import org.jage.communication.message.Messages;
import org.jage.platform.argument.RuntimeArgumentsService;
import org.jage.platform.component.IStatefulComponent;
import org.jage.platform.component.definition.ConfigurationException;
import org.jage.platform.component.definition.IComponentDefinition;
import org.jage.platform.component.provider.IComponentInstanceProvider;
import org.jage.platform.component.provider.IComponentInstanceProviderAware;
import org.jage.platform.config.loader.IConfigurationLoader;
import org.jage.services.core.ConfigurationService;
import org.jage.services.core.LifecycleManager;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

/**
 * A default configuration service that works in three possible modes:
 * <ul>
 * <li>as local-only configuration service when it does nothing (it assumes that configuration has been already loaded
 * in the node configuration);
 * <li>as distributed - waiting for the configuration from other nodes;
 * <li>as distributed - sending configuration to other nodes.
 * </ul>
 *
 * @author AGH AgE Team
 */
public class DefaultConfigurationService implements ConfigurationService, IComponentInstanceProviderAware,
        IStatefulComponent {

	private static final Logger log = LoggerFactory.getLogger(DefaultConfigurationService.class);

	private static final String COMPONENT_NAME = DefaultConfigurationService.class.getSimpleName();

	private static final String COMPUTATION_CONFIGURATION = "age.computation.conf";

	private IComponentInstanceProvider instanceProvider;

	@Inject
	private CommunicationService communicationService;

	@Inject
	private NodeAddressSupplier nodeAddressProvider;

	@Inject
	private RuntimeArgumentsService argumentsService;

	private final ListeningScheduledExecutorService service = MoreExecutors
	        .listeningDecorator(newSingleThreadScheduledExecutor());

	private ComponentAddress serviceAddress;

	@Inject
	private LifecycleManager lifecycleManager;

	/**
	 * Distributes provided component definitions among other nodes participating in the computation.
	 * <p>
	 *
	 * Note: this implementation does not care whether anyone received the configuration.
	 *
	 * @param componentDefinitions
	 *            the computation configuration to distribute.
	 */
	public void distributeConfiguration(final Collection<IComponentDefinition> componentDefinitions) {
		// Ensure serializable collection.
		final ArrayList<IComponentDefinition> definitions = newArrayList(checkNotNull(componentDefinitions));

		serviceAddress = new DefaultComponentAddress(COMPONENT_NAME, nodeAddressProvider.get());
		final BroadcastSelector<ComponentAddress> receiversSelector = new BroadcastSelector<ComponentAddress>();

		final IHeader<ComponentAddress> header = Header.create(serviceAddress, receiversSelector);
		final IMessage<ComponentAddress, ArrayList<IComponentDefinition>> message = Message
		        .create(header, definitions);

		communicationService.send(message);

		notifyAboutConfigurationChange(definitions);
	}

	/**
	 * Receives a configuration from a remote node. It expects the configuration to be an array list of component
	 * definitions.
	 */
	public void receiveConfiguration() {
		final Callable<ArrayList<IComponentDefinition>> query = new Callable<ArrayList<IComponentDefinition>>() {
			@Override
			public ArrayList<IComponentDefinition> call() throws Exception {
				IMessage<ComponentAddress, Serializable> message = null;
				while (message == null) {
					message = communicationService.receive(COMPONENT_NAME);
					Thread.sleep(1000);
				}
				return Messages.getPayloadOfTypeOrThrow(message, ArrayList.class);
			}
		};

		final ListenableFuture<ArrayList<IComponentDefinition>> future = service.submit(query);

		// Add an action performed when a message is received.
		Futures.addCallback(future, new FutureCallback<ArrayList<IComponentDefinition>>() {
			@Override
			public void onSuccess(final ArrayList<IComponentDefinition> result) {
				notifyAboutConfigurationChange(result);
			}

			@Override
			public void onFailure(final Throwable t) {
				log.error("Could not receive a new configuration.", t);
			}
		});
	}

	@Override
	public void setInstanceProvider(final IComponentInstanceProvider instanceProvider) {
		this.instanceProvider = instanceProvider;
	}

	private void notifyAboutConfigurationChange(final List<IComponentDefinition> result) {
		final List<IComponentDefinition> definitions = Collections.unmodifiableList(result);
		lifecycleManager.onNewComputationConfiguration(false, definitions);
	}

	@Override
	public void init() {
		communicationService = instanceProvider.getInstance(CommunicationService.class);
		nodeAddressProvider = instanceProvider.getInstance(NodeAddressSupplier.class);
	}

	@Override
	public boolean finish() {
		service.shutdown();
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			log.error("The executor service could not be properly shutdown.", e);
			Thread.currentThread().interrupt();
		}
		return true;
	}

	@Override
	public void obtainConfiguration() throws ConfigurationException {
		log.info("Configuration service is starting configuration.");
		final String configFilePath = argumentsService.getCustomOption(COMPUTATION_CONFIGURATION);
		if (communicationService.isDistributedEnvironment()) {
			if (configFilePath == null) {
				log.debug("No configuration provided. I will be waiting for it.");

				receiveConfiguration();
			} else {
				log.debug("A computation configuration provided as a file {}.", configFilePath);

				final Collection<IComponentDefinition> computationComponents = instanceProvider.getInstance(
				        IConfigurationLoader.class).loadConfiguration(configFilePath);
				distributeConfiguration(computationComponents);
			}
		} else {
			log.debug("Assuming that the computation configuration was provided in the node configuration.");
			lifecycleManager.onNewComputationConfiguration(true, null);
		}
	}

}
