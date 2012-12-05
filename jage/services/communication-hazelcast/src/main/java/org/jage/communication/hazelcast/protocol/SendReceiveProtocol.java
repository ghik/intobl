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
 * $Id: SendReceiveProtocol.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast.protocol;

import java.io.Serializable;
import java.util.Set;

import static java.util.Collections.singleton;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddress;
import org.jage.address.selector.IAddressSelector;
import org.jage.communication.common.cache.AddressCache;
import org.jage.communication.common.protocol.MessageReceivedListener;
import org.jage.communication.message.IMessage;
import org.jage.communication.message.Messages;

import static org.jage.address.selector.Selectors.isMultiTarget;
import static org.jage.communication.message.Messages.multitargetToUnicast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

/**
 * A Hazelcast based communication protocol implementing a topic based send/receive model.
 * <p>
 * In other words, every receiver has it's own dedicated unicast topic, identified by a string which is a variation of
 * the receiver's AgE address. This topic is a Hazelcast's distributed topic.
 *
 * @author AGH AgE Team
 */
public class SendReceiveProtocol extends HazelcastCommonProtocol implements
        MessageListener<IMessage<ComponentAddress, Serializable>> {

	private static final Logger log = LoggerFactory.getLogger(SendReceiveProtocol.class);

	private ITopic<IMessage<ComponentAddress, Serializable>> myUnicast;

	@Inject
	private AddressCache addressCache;

	/**
	 * Creates a new Hazelcast-based send-receive protocol.
	 */
	public SendReceiveProtocol() {
		super();
	}

	/**
	 * Package-visible constructor for mocking.
	 *
	 * @param hazelcastInstance
	 *            a Hazelcast instance to use.
	 */
	SendReceiveProtocol(@Nullable final HazelcastInstance hazelcastInstance) {
		super(hazelcastInstance);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Connects this protocol to it's dedicated unicast topic.
	 */
	@Override
	public void init() {
		log.info("Initializing the send-receive protocol.");
		super.init();

		myUnicast = getHazelcastInstance().getTopic(getUnicastChannelFor(getLocalAddress()));
		myUnicast.addMessageListener(this);

		log.info("Done with initialization of the send-receive protocol.");
	}

	/**
	 * Destroys the dedicated unicast channel.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public boolean finish() {
		log.info("Finalizing the send-receive protocol.");

		// destroy the unicast channel
		myUnicast.destroy();

		super.finish();

		log.info("Done with finalization of the send-receive protocol.");

		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation will translate multi-target messages to unicast messages.
	 */
	@Override
	public void send(final IMessage<ComponentAddress, Serializable> message) {
		final IAddressSelector<ComponentAddress> selector = message.getHeader().getReceiverSelector();
		Set<IMessage<ComponentAddress, Serializable>> messages = null;
		if (isMultiTarget(selector)) {
			messages = multitargetToUnicast(message);
		} else {
			messages = singleton(message);
		}

		for (final IMessage<ComponentAddress, Serializable> msg : messages) {
			final String channel = getUnicastChannelFor(Messages.getOnlyReceiverAddress(msg).getNodeAddress());
			final ITopic<IMessage<ComponentAddress, Serializable>> topic = getHazelcastInstance().getTopic(channel);

			log.debug("Publishing message {}.", msg);
			topic.publish(msg);
		}
	}

	/**
	 * Callback for arrival of unicast messages sent to the protocol's channel.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public void onMessage(final com.hazelcast.core.Message<IMessage<ComponentAddress, Serializable>> hazelcastMessage) {
		final IMessage<ComponentAddress, Serializable> message = hazelcastMessage.getMessageObject();
		log.debug("Received message {}.", message);

		for (final MessageReceivedListener listener : getListeners()) {
			listener.onMessageReceived(message);
		}
	}

	/**
	 * Generates a channel name from the node address.
	 *
	 * @param address
	 *            A node address to use.
	 * @return name of the unicast channel for the given node address.
	 */
	public static String getUnicastChannelFor(final NodeAddress address) {
		return "jd-" + address;
	}

	@Override
	public boolean isStarting() {
		return myUnicast == null;
	}
}
