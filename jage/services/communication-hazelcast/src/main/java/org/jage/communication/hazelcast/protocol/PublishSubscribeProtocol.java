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
 * $Id: PublishSubscribeProtocol.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast.protocol;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.component.ComponentAddress;
import org.jage.communication.common.protocol.MessageReceivedListener;
import org.jage.communication.message.IMessage;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;

/**
 * A Hazelcast based communication protocol implementing a topic based publish / subscribe model.
 * <p>
 * There is one only one topic for messages, common to all the PublishSubscribeStrategy components in the environment.
 * All those components are listening to incoming messages at the same time but they are handling only those messages
 * which were destined for them. In other words, every protocol is constantly filtering the common channel to get only
 * the messages it's interested in.
 * <p>
 * The common topic is a Hazelcast's distributed topic.
 * 
 * @author AGH AgE Team
 */
public class PublishSubscribeProtocol extends HazelcastCommonProtocol implements
        MessageListener<IMessage<ComponentAddress, Serializable>> {

	private static final Logger log = LoggerFactory.getLogger(PublishSubscribeProtocol.class);

	private static final String CHANNEL = "jd-business";

	private ITopic<IMessage<ComponentAddress, Serializable>> sharedTopic;

	/**
	 * Creates a new Hazelcast-based pub-sub protocol.
	 */
	public PublishSubscribeProtocol() {
		super();
	}

	/**
	 * Package-visible constructor for mocking.
	 * 
	 * @param hazelcastInstance
	 *            a Hazelcast instance to use.
	 */
	PublishSubscribeProtocol(@Nullable final HazelcastInstance hazelcastInstance) {
		super(hazelcastInstance);
	}

	/**
	 * Connects this protocol to the common unicast topic.
	 * <p>
	 * Also, gets the local AgE address from the provider.
	 */
	@Override
	public void init() {
		log.info("Initializing the pub-sub protocol.");
		super.init();

		sharedTopic = getHazelcastInstance().getTopic(CHANNEL);
		sharedTopic.addMessageListener(this);

		log.info("Done with initialization of the pub-sub protocol.");
	}

	@Override
	public boolean finish() {
		log.info("Finalizing the pub-sub protocol.");

		super.finish();

		log.info("Done with finalization of the pub-sub protocol.");

		return true;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation sends messages as-is (without converting them to unicast messages).
	 */
	@Override
	public void send(final IMessage<ComponentAddress, Serializable> message) {
		log.debug("Publishing message {}.", message);
		sharedTopic.publish(message);
	}

	/**
	 * Callback for arrival of unicast messages sent to the business channel.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public void onMessage(final com.hazelcast.core.Message<IMessage<ComponentAddress, Serializable>> hazelcastMessage) {
		final IMessage<ComponentAddress, Serializable> message = hazelcastMessage.getMessageObject();
		log.debug("Received message {}.", message);
		
		for (MessageReceivedListener listener : getListeners()) {
			listener.onMessageReceived(message);
		}
	}

	@Override
	public boolean isStarting() {
		return sharedTopic == null;
	}
}
