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
 * $Id: SendReceiveProtocolTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast.protocol;

import java.io.Serializable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddressSupplier;
import org.jage.address.node.DefaultNodeAddress;
import org.jage.communication.common.protocol.MessageReceivedListener;
import org.jage.communication.message.IMessage;
import org.jage.communication.message.MessageTestUtils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

/**
 * Tests for the {@link SendReceiveProtocol} class.
 * 
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class SendReceiveProtocolTest {

	@Mock
	private NodeAddressSupplier addressProvider;
	
	@Mock
	private HazelcastInstance hazelcastInstance;

	@InjectMocks
	private SendReceiveProtocol sendrecvProtocol = new SendReceiveProtocol(null);
	
	@Mock
	private ITopic<IMessage<ComponentAddress, Serializable>> topic;

	@Mock
	private com.hazelcast.core.Message<IMessage<ComponentAddress, Serializable>> hazelcastMessage;

	@Test
	public void testMessageListenerInteraction() {
		// given
		DefaultNodeAddress address = new DefaultNodeAddress("test1", "hostname");
		MessageReceivedListener listener = mock(MessageReceivedListener.class);
		IMessage<ComponentAddress, Serializable> message = MessageTestUtils.createEmptyComponentMessage("from", "to",
		        address);

		given(hazelcastInstance.<IMessage<ComponentAddress, Serializable>> getTopic(anyString())).willReturn(topic);
		given(hazelcastMessage.getMessageObject()).willReturn(message);
		given(addressProvider.get()).willReturn(address);

		// when
		sendrecvProtocol.addMessageReceivedListener(listener);
		sendrecvProtocol.init();
		sendrecvProtocol.onMessage(hazelcastMessage);

		// then
		verify(listener, only()).onMessageReceived(message);
	}
	
	// XXX: Methods below are to be moved to integration tests.
	
//	public void testMessageListenerInteraction() {
//		// given
//		System.setProperty("hazelcast.wait.seconds.before.join", "0");
//		System.setProperty("hazelcast.initial.wait.seconds", "0");
//		NodeAddress address = new NodeAddress("test1", "hostname");
//		MessageReceivedListener listener = mock(MessageReceivedListener.class);
//		IMessage<IComponentAddress, Serializable> message = MessageTestUtils.createEmptyComponentMessage("from", "to",
//		        address);
//
//		given(hazelcastMessage.getMessageObject()).willReturn(message);
//		given(addressProvider.getNodeAddress()).willReturn(address);
//
//		// when
//		sendrecvProtocol.addMessageReceivedListener(listener);
//		sendrecvProtocol.init();
//		sendrecvProtocol.onMessage(hazelcastMessage);
//
//		// then
//		verify(listener, only()).onMessageReceived(message);
//	}
//
//	public void teardown() {
//		Hazelcast.shutdownAll();
//	}
}
