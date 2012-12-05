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
 * Created: 2012-08-20
 * $Id: LoopbackCommunicationService.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.component.ComponentAddress;
import org.jage.address.selector.IAddressSelector;
import org.jage.address.selector.UnicastSelector;
import org.jage.communication.common.manager.CommunicationManager;
import org.jage.communication.message.IMessage;
import org.jage.platform.component.provider.IComponentInstanceProvider;
import org.jage.platform.component.provider.IComponentInstanceProviderAware;

import static org.jage.address.selector.Selectors.getOnlyAddress;
import static org.jage.address.selector.Selectors.isMultiTarget;
import static org.jage.communication.message.Messages.getSenderAddress;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

/**
 * An local-only implementation of the communication service.
 * 
 * @author AGH AgE Team
 */
public class LoopbackCommunicationService implements CommunicationService, IComponentInstanceProviderAware {

	private static final Logger log = LoggerFactory.getLogger(LoopbackCommunicationService.class);

	private IComponentInstanceProvider instanceProvider;

	// the queue for messages destined for asynchronous clients (those who do
	// not implement the IncomingMessageListener interface)
	private final Multimap<String, IMessage<ComponentAddress, Serializable>> received = Multimaps
	        .synchronizedMultimap(HashMultimap.<String, IMessage<ComponentAddress, Serializable>> create());

	private final Map<String, IncomingMessageListener> aliasedListeners = newHashMap();

	@SuppressWarnings("unchecked")
	@Override
	public void send(IMessage<? extends ComponentAddress, ? extends Serializable> message) {
		deliverIncomingMessage((IMessage<ComponentAddress, Serializable>)message);
	}

	@Override
	@CheckForNull
	public IMessage<ComponentAddress, Serializable> receive(String componentName) {
		final Collection<IMessage<ComponentAddress, Serializable>> list = received.get(componentName);

		synchronized (received) {
			final Iterator<IMessage<ComponentAddress, Serializable>> iter = list.iterator();
			if (iter.hasNext()) {
				final IMessage<ComponentAddress, Serializable> result = iter.next();
				iter.remove();

				return result;
			}
			return null;
		}
	}

	@Override
	public void barrier(String key) throws InterruptedException {
		// Empty
	}

	@Override
	public void addMessageListener(final String componentName, final IncomingMessageListener listener) {
		aliasedListeners.put(checkNotNull(componentName), checkNotNull(listener));
	}

	@Override
	public long getNodesCount() {
		return 1;
	}

	@Override
	public boolean isLocalNodeMaster() {
		return true;
	}

	/**
	 * Delivers the given message.
	 * 
	 * <p>
	 * CommunicationService deals with delivering of messages in two ways:
	 * <ul>
	 * <li>if the message was sent to a component which implements the IncomingMessageListener interface, we have a
	 * "push" model; we deliver the message now and forget about it
	 * <li>otherwise, we have a "pull" model; we cache the message - the client component can query us for its messages
	 * later
	 * </ul>
	 */
	private void deliverIncomingMessage(final IMessage<ComponentAddress, Serializable> message) {
		final ComponentAddress address;

		final IAddressSelector<ComponentAddress> selector = message.getHeader().getReceiverSelector();
		if (isMultiTarget(selector)) {
			address = getSenderAddress(message);
		} else {
			address = getOnlyAddress((UnicastSelector<ComponentAddress>)selector);
		}
		final String nameOfClient = address.getIdentifier();

		IncomingMessageListener listener = aliasedListeners.get(nameOfClient);
		if (listener == null) {
			final Object client = instanceProvider.getInstance(nameOfClient);
			if (client instanceof IncomingMessageListener) {
				listener = (IncomingMessageListener)client;
			}
		}
		if (listener == null) {
			log.debug("No such client component: {}. I will remember his message anyway.", nameOfClient);
			received.put(nameOfClient, message);
		} else {
			listener.deliver(message);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation of {@link CommunicationManager} requires the provider to deliver messages to its synchronous
	 * (i.e. implementing the {@link IncomingMessageListener} interface) clients.
	 */
	@Override
	public void setInstanceProvider(final IComponentInstanceProvider instanceProvider) {
		this.instanceProvider = instanceProvider;
	}

	@Override
	public boolean isDistributedEnvironment() {
		return false;
	}
}
