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
 * $Id: MessageTestUtils.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.message;

import java.io.Serializable;

import static org.mockito.Mockito.mock;

import org.jage.address.component.DefaultComponentAddress;
import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddress;
import org.jage.address.selector.UnicastSelector;

/**
 * Utilities for message-related tests.
 * 
 * @author AGH AgE Team
 */
public final class MessageTestUtils {

	/**
	 * Constructs an empty message from a component to another component.
	 * 
	 * @param fromComponent
	 *            a name of the sending component.
	 * @param toComponent
	 *            a name of the receiving component.
	 * @return An empty message.
	 */
	public static IMessage<ComponentAddress, Serializable> createEmptyComponentMessage(String fromComponent,
	        String toComponent) {
		return createEmptyComponentMessage(fromComponent, toComponent, mock(NodeAddress.class));
	}

	/**
	 * Constructs an empty message from a component to another component in the given node.
	 * 
	 * @param fromComponent
	 *            a name of the sending component.
	 * @param toComponent
	 *            a name of the receiving component.
	 * @param toNodeAddress
	 *            an address of the target node.
	 * @return An empty message.
	 */
	public static IMessage<ComponentAddress, Serializable> createEmptyComponentMessage(String fromComponent,
	        String toComponent, NodeAddress toNodeAddress) {
		return createComponentMessageWithPayload(fromComponent, toComponent, toNodeAddress, (Serializable)"payload");
	}

	/**
	 * Constructs an empty message from a component to another component in the given node.
	 * 
	 * @param fromComponent
	 *            a name of the sending component.
	 * @param toComponent
	 *            a name of the receiving component.
	 * @param toNodeAddress
	 *            an address of the target node.
	 * @param payload
	 *            a payload to put into the message.
	 * @return a message with the given payload.
	 */
	public static <T extends Serializable> IMessage<ComponentAddress, T> createComponentMessageWithPayload(
	        String fromComponent, String toComponent, NodeAddress toNodeAddress, T payload) {
		NodeAddress sourceNodeAddress = mock(NodeAddress.class);
		ComponentAddress sourceComponentAddress = new DefaultComponentAddress(fromComponent, sourceNodeAddress);

		NodeAddress targetNodeAddress = toNodeAddress;
		ComponentAddress targetComponentAddress = new DefaultComponentAddress(toComponent, targetNodeAddress);

		IHeader<ComponentAddress> header = new Header<ComponentAddress>(sourceComponentAddress,
		        new UnicastSelector<ComponentAddress>(targetComponentAddress));
		return new Message<ComponentAddress, T>(header, payload);
	}
}
