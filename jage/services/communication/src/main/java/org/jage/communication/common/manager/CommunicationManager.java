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
 * $Id: CommunicationManager.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.manager;

import java.io.Serializable;

import org.jage.address.component.ComponentAddress;
import org.jage.communication.CommunicationService;
import org.jage.communication.common.protocol.MessageReceivedListener;
import org.jage.communication.message.IMessage;
import org.jage.platform.component.IStatefulComponent;

/**
 * An extended CommunicationService interface for implementations that obey the communication-common's contract and
 * architecture.
 * <p>
 * The skeletal service is an active, stateful and componentProvider-aware AgE component. Being an active component, it
 * implements Runnable.
 * <p>
 * Every CommunicationManager has two queues and deals with all the details of handling those:
 * <ul>
 * <li>the first queue is for messages that are about to be sent; in other words, some local component has issued a
 * "send" request but the CommunicationManager didn't handle it yet; that's the "sending queue"
 * <li>the second queue is for messages that are about to be delivered; in other words, some remote component has sent a
 * message to a local component but the CommunicationManager didn't deliver the message yet even though it had already
 * arrived to the local node; that's the "delivering queue"
 * </ul>
 * <p>
 * CommunicationManager is dealing with low level details of handling the communication inside a AgE environment like
 * selector initialization, synchronous and asynchronous receivers etc.
 * <p>
 * CommunicationManager sends messages physically only using an CommunicationProtocol. It also listens to the
 * "onMessage" events sent by the protocol in order to receive messages that are coming from other nodes.
 * <p>
 * When it needs to initialize a AgE selector, it uses an IAddressCache. The cache may be a set (IAddressSet) or a map
 * (IAddressMap), a communication service doesn't care about this.
 * 
 * @author AGH AgE Team
 */
public interface CommunicationManager extends IStatefulComponent, Runnable, CommunicationService,
		MessageReceivedListener {

	/**
	 * Initializes this CommunicationManager. Should start the queue handling loop.
	 */
	@Override
	void init();

	/**
	 * Stops this CommunicationManager, possibly with a short delay.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	boolean finish();

	/**
	 * The queue handling thread of this CommunicationManager.
	 */
	@Override
	void run();

	/**
	 * {@inheritDoc}
	 * <p>
	 * Every CommunicationManager listens to events about messages that CommunicationProtocol receives from other
	 * nodes. Every service delivers those messages to their local receivers.
	 * <p>
	 * CommunicationManager must register at an CommunicationProtocol as a listener of those events!
	 */
	@Override
	void onMessageReceived(IMessage<ComponentAddress, Serializable> message);

}
