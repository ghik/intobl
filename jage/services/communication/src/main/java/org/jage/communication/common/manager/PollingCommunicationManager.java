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
 * Created: 2012-02-07
 * $Id: PollingCommunicationManager.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddressSupplier;
import org.jage.address.node.NodeAddress;
import org.jage.address.selector.IAddressSelector;
import org.jage.address.selector.UnicastSelector;
import org.jage.communication.IncomingMessageListener;
import org.jage.communication.common.cache.AddressCache;
import org.jage.communication.common.protocol.CommunicationProtocol;
import org.jage.communication.common.scanner.NeighbourhoodScanner;
import org.jage.communication.message.IMessage;
import org.jage.platform.component.provider.IComponentInstanceProvider;
import org.jage.platform.component.provider.IComponentInstanceProviderAware;

import static org.jage.address.selector.Selectors.getOnlyAddress;
import static org.jage.address.selector.Selectors.isMultiTarget;
import static org.jage.communication.message.Messages.getSenderAddress;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.consumingIterable;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Maps.newHashMap;

/**
 * A polling implementation of CommunicationManager.
 * <p>
 * After a PollingCommunicationManager is initialized, it will every few seconds send all the messages from the
 * "sending queue" to other nodes and also deliver all the messages from the "delivering queue" to their corresponding
 * receivers on the local node.
 * <p>
 * This means that PollingCommunicationManager does all its work asynchronously and in a polling manner thus every
 * requested operation might be slightly delayed.
 *
 * @author AGH AgE Team
 */
public class PollingCommunicationManager implements CommunicationManager, IComponentInstanceProviderAware {

	private static final Logger log = LoggerFactory.getLogger(PollingCommunicationManager.class);

	private NodeAddress localAddress;

	private IComponentInstanceProvider instanceProvider;

	@Inject
	private NodeAddressSupplier addressProvider;

	@Inject
	private CommunicationProtocol communicationProtocol;

	@Inject
	private AddressCache addressCache;

	@Inject
	private NeighbourhoodScanner neighbourhoodScanner;

	// the "sending queue"
	private final List<IMessage<ComponentAddress, Serializable>> toSend = Collections
	        .synchronizedList(new LinkedList<IMessage<ComponentAddress, Serializable>>());

	// the "delivering queue"
	private final List<IMessage<ComponentAddress, Serializable>> toDeliver = Collections
	        .synchronizedList(new LinkedList<IMessage<ComponentAddress, Serializable>>());

	// the queue for messages destined for asynchronous clients (those who do
	// not implement the IncomingMessageListener interface)
	private final Multimap<String, IMessage<ComponentAddress, Serializable>> received = Multimaps
	        .synchronizedMultimap(HashMultimap.<String, IMessage<ComponentAddress, Serializable>> create());

	private final Map<String, IncomingMessageListener> aliasedListeners = newHashMap();

	private volatile boolean finished = false;

	private long stepTime = 1000;

	private Thread pollingThread;

	/**
	 * Registers the communication service as an MessageReceivedListener with the ICommunicationStrategy from AgE's
	 * context.
	 * <p>
	 * Also, starts the queue handling thread and gets the local AgE address from the instance provider.
	 */
	@Override
	public void init() {
		log.info("Initializing the PollingCommunicationManager...");

		// listen for the protocol's "message-received" events
		communicationProtocol.addMessageReceivedListener(this);

		// get the local AgE address
		localAddress = addressProvider.get();

		pollingThread = new Thread(this);
		pollingThread.start();

		log.info("Done with initialization of the PollingCommunicationManager...");
	}

	@Override
	public boolean finish() {
		log.info("Finalizing the PollingCommunicationManager.");

		finished = true;
		try {
			pollingThread.join(4 * stepTime);
		} catch (final InterruptedException e) {
			log.warn("Interrupted", e);
			return false;
		}

		log.info("Done with finalization of the PollingCommunicationManager.");
		return true;
	}

	@Override
	public void run() {
		try {
			waitForDependencies();
			while (!finished) {
				sendOutgoingMessages();
				deliverIncomingMessages();

				Thread.sleep(stepTime);
			}
		} catch (final InterruptedException e) {
			log.warn("Interrupted", e);
			return;
		}
	}

	private void waitForDependencies() throws InterruptedException {
		while (!finished && (neighbourhoodScanner.isStarting() || communicationProtocol.isStarting())) {
			log.trace("Dependencies still not alive.");
			Thread.sleep(4 * stepTime);
		}
	}

	/**
	 * Sends all the queued messages.
	 */
	private void sendOutgoingMessages() {
		synchronized (toSend) {
			try {
				final Iterator<IMessage<ComponentAddress, Serializable>> iter = toSend.iterator();
				while (iter.hasNext()) {
					final IMessage<ComponentAddress, Serializable> message = iter.next();
					message.getHeader().getReceiverSelector().initialize(addressCache.getAddressesOfNeighbours(), null);
					log.debug("Outgoing message: {}.", message);
					communicationProtocol.send(message);
					iter.remove();
				}
			} catch (final Exception ex) {
				// TODO: We need to think over error handling.
				if (communicationProtocol.isStopped()) {
					log.warn("Seems that protocol is already stopped. That means there is some problem. Sorry.");
					finished = true;
				} else {
					// log the error; we'll try to send the message later
					log.error("Could not send the message.", ex);
				}
			}
		}
	}

	/**
	 * Delivers all queued messages.
	 */
	private void deliverIncomingMessages() {
		synchronized (toDeliver) {
			final Iterator<IMessage<ComponentAddress, Serializable>> iter = toDeliver.iterator();
			while (iter.hasNext()) {
				final IMessage<ComponentAddress, Serializable> msg = iter.next();
				try {
					deliverIncomingMessage(msg);
					iter.remove();
				} catch (final Exception ex) {
					// log the error; we'll try to deliver the message later
					log.error("Could not deliver the message to the local component.", ex);
				}
			}
		}
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

	@SuppressWarnings("unchecked")
	@Override
	public void send(final IMessage<? extends ComponentAddress, ? extends Serializable> message) {
		toSend.add((IMessage<ComponentAddress, Serializable>)message);
	}

	@Override
	public IMessage<ComponentAddress, Serializable> receive(final String clientId) {
		synchronized (received) {
			final Iterable<IMessage<ComponentAddress, Serializable>> messages = consumingIterable(received
			        .get(clientId));
			return getFirst(messages, null);
		}
	}

	/**
	 * Adds a message that the communication manager has just received to the "delivering queue".
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public void onMessageReceived(final IMessage<ComponentAddress, Serializable> message) {
		final IAddressSelector<ComponentAddress> selector = message.getHeader().getReceiverSelector();
		if (isMultiTarget(selector)) {
			for (final ComponentAddress address : selector.addresses()) {
				final NodeAddress nodeAddress = address.getNodeAddress();
				if (nodeAddress.equals(localAddress)) {
					toDeliver.add(message);
					break;
				}
			}
		} else {
			final ComponentAddress address = getOnlyAddress((UnicastSelector<ComponentAddress>)selector);
			if (address.getNodeAddress().equals(localAddress)) {
				toDeliver.add(message);
			}
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

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation may block even without creating the barrier - when the underlying protocol is not
	 * initialised.
	 *
	 * @throws NullPointerException
	 *             if the key is {@code null}.
	 */
	@Override
	public void barrier(final String key) throws InterruptedException {
		checkNotNull(key);
		waitForDependencies();
		communicationProtocol.barrier(key, addressCache.getNodesCount());
	}

	void setStepTime(final long stepTime) {
		checkArgument(stepTime >= 0, "Time of the step must be nonnegative.");
		this.stepTime = stepTime;
	}

	@Override
	public void addMessageListener(final String componentName, final IncomingMessageListener listener) {
		aliasedListeners.put(checkNotNull(componentName), checkNotNull(listener));
	}

	@Override
	public long getNodesCount() {
		return addressCache.getNodesCount();
	}

	@Override
	public boolean isLocalNodeMaster() {
		final Collection<NodeAddress> allNodes = addressCache.getAddressesOfAllNodes();
		final NodeAddress min = Ordering.natural().min(allNodes);
		log.debug("Master is {}.", min);
		return min.equals(localAddress);
	}

	@Override
	public boolean isDistributedEnvironment() {
		return true;
	}

}
