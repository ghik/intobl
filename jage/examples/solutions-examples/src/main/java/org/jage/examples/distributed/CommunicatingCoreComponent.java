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
 * Created: 2012-02-09
 * $Id: CommunicatingCoreComponent.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.examples.distributed;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

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
import org.jage.platform.component.definition.IComponentDefinition;
import org.jage.platform.component.exception.ComponentException;
import org.jage.services.core.CoreComponent;
import org.jage.services.core.ICoreComponentListener;

import com.google.common.collect.Sets;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is an example component that uses the communication service to send and receive messages. It simply tries to
 * send 10 broadcast messages and checks its delivery queue the same number of times.
 *
 * @author AGH AgE Team
 */
public class CommunicatingCoreComponent implements CoreComponent {

	private static final Logger log = LoggerFactory.getLogger(CommunicatingCoreComponent.class);

	private final Set<ICoreComponentListener> listeners = Sets.newHashSet();

	private ComponentAddress myAddress;

	private static final String NAME = "communicatingCoreComponent";

	@Inject
	private CommunicationService communicationService;

	@Inject
	private NodeAddressSupplier addressProvider;

	@Override
	public void init() throws ComponentException {
		myAddress = new DefaultComponentAddress(NAME, addressProvider.get());
	}

	@Override
	public boolean finish() throws ComponentException {
		// Empty
		return false;
	}

	@Override
	public void start() throws ComponentException {
		for (final ICoreComponentListener listener : listeners) {
			listener.onCoreComponentStarting(this);
		}

		for (int i = 0; i < 10; i++) {
			final IHeader<ComponentAddress> header = new Header<ComponentAddress>(myAddress,
			        new BroadcastSelector<ComponentAddress>());
			final IMessage<ComponentAddress, Serializable> message = new Message<ComponentAddress, Serializable>(header,
			        "Hello world!");

			log.info("Sending message: {}.", message);
			communicationService.send(message);

			try {
				Thread.sleep(2000);
			} catch (final InterruptedException e) {
				log.error("Interrupted.", e);
			}

			final IMessage<ComponentAddress, Serializable> receivedMessage = communicationService.receive(NAME);
			log.info("Received message: {}.", receivedMessage);
		}

		for (final ICoreComponentListener listener : listeners) {
			listener.onCoreComponentStopped(this);
		}
	}

	@Override
	public void stop() {
		// Empty
	}

	@Override
	public void registerListener(final ICoreComponentListener listener) {
		checkNotNull(listener);
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(final ICoreComponentListener listener) {
		checkNotNull(listener);
		listeners.remove(listener);
	}

	@Override
	public void computationConfigurationUpdated(final Collection<IComponentDefinition> componentDefinitions) {
		// Empty

	}

	@Override
	public void pause() {
		// Empty
	}

	@Override
	public void resume() {
		// Empty
	}

	@Override
	public void teardownConfiguration() {
		// Empty
	}

}
