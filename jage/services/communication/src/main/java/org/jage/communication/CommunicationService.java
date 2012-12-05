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
 * Created: 2010-09-08
 * $Id: CommunicationService.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication;

import java.io.Serializable;

import javax.annotation.CheckForNull;

import org.jage.address.component.ComponentAddress;
import org.jage.communication.message.IMessage;

/**
 * Simple interface for a communication service which provides communication between nodes in distributed environment.
 * It is particularly provided for all components that want to use communication service explicitly.
 * 
 * @author AGH AgE Team
 */
public interface CommunicationService {

	/**
	 * Sends the message to another component, possibly located in another node.
	 * 
	 * @param message
	 *            A message to send, cannot be <code>null</code>.
	 */
	void send(IMessage<? extends ComponentAddress, ? extends Serializable> message);

	/**
	 * Receives a next message for a given component name. This is non-blocking method --- if there is no message for
	 * the component, <code>null</code> is returned instead.
	 * <p>
	 * Please note, that if component realizes the {@link IncomingMessageListener} interface, this method will most of
	 * the time return <code>null</code>, as messages will be delivered through the listener.
	 * 
	 * @param componentName
	 *            A name of the requesting component.
	 * @return A new message or <code>null</code>.
	 */
	@CheckForNull
	IMessage<ComponentAddress, Serializable> receive(String componentName);

	/**
	 * Enters a global barrier. "Global" means that all nodes in the known environment must participate. The barrier is
	 * created for the given key (that needs to be common among all the nodes). Different node services may (and should)
	 * use different keys to block on independent barriers.
	 * <p>
	 * 
	 * This call blocks a current thread until all of the nodes call it with the same key.
	 * <p>
	 * 
	 * If any node is interrupted while waiting, then all other waiting nodes will throw {@link CommunicationException}
	 * and the barrier is destroyed.
	 * 
	 * @param key
	 *            a key for the barrier. Must be the same for all the participating nodes.
	 * 
	 * @throws InterruptedException
	 *             if the thread waiting on the barrier was interrupted.
	 * @throws CommunicationException
	 *             if the barrier cannot be created or when it was destroyed due to any reason.
	 */
	void barrier(String key) throws InterruptedException;

	/**
	 * Adds a listener for messages targeted for a given component name.
	 * 
	 * @param componentName
	 *            a name of a component for which messages are to be received by the listener.
	 * @param listener
	 *            a listener.
	 */
	void addMessageListener(String componentName, IncomingMessageListener listener);
	
	/**
	 * Returns a count of currently known nodes in the environment.
	 * 
	 * @return a count of currently known nodes in the environment.
	 */
	long getNodesCount();
	
	boolean isLocalNodeMaster();
	
	boolean isDistributedEnvironment();
}
