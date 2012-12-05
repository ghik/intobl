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
 * $Id: PollingCommunicationManagerTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.manager;

import java.io.Serializable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddressSupplier;
import org.jage.address.node.NodeAddress;
import org.jage.communication.common.cache.AddressSet;
import org.jage.communication.common.protocol.CommunicationProtocol;
import org.jage.communication.common.scanner.NeighbourhoodScanner;
import org.jage.communication.message.IMessage;
import org.jage.communication.message.MessageTestUtils;
import org.jage.communication.message.Messages;
import org.jage.platform.component.provider.IComponentInstanceProvider;

/**
 * Tests for the {@link PollingCommunicationManager} class.
 * 
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class PollingCommunicationManagerTest {

	@Mock
	private IComponentInstanceProvider instanceProvider;

	@Mock
	private NodeAddress localNodeAddress;

	@Mock
	private AddressSet addressSet;

	@Mock
	private NodeAddressSupplier addressProvider;

	@Mock
	private CommunicationProtocol communicationProtocol;

	@Mock
	private NeighbourhoodScanner neighbourhoodScanner;

	@InjectMocks
	private PollingCommunicationManager communicationManager;

	@Test
	public void testSend() throws InterruptedException {
		// given
		given(addressProvider.get()).willReturn(localNodeAddress);
		communicationManager.setStepTime(0);
		communicationManager.init();

		IMessage<ComponentAddress, Serializable> message = MessageTestUtils.createEmptyComponentMessage("from", "to",
		        localNodeAddress);

		// when
		communicationManager.send(message);

		// then
		Thread.sleep(500);
		verify(communicationProtocol).addMessageReceivedListener(communicationManager);
		verify(communicationProtocol).send(any(IMessage.class));
	}

	@Test
	public void testReceive() throws InterruptedException {
		// given
		given(addressProvider.get()).willReturn(localNodeAddress);
		communicationManager.setStepTime(0);
		communicationManager.init();

		IMessage<ComponentAddress, Serializable> message = MessageTestUtils.createEmptyComponentMessage("from", "to",
		        localNodeAddress);
		ComponentAddress receiverAddress = Messages.getOnlyReceiverAddress(message);

		// when
		communicationManager.onMessageReceived(message);
		Thread.sleep(500);
		IMessage<ComponentAddress, Serializable> received = communicationManager.receive("to");

		// then
		assertThat(received, is(notNullValue()));
		assertThat(received.getPayload(), equalTo(message.getPayload()));
		assertThat(received.getHeader().getReceiverSelector().selected(receiverAddress), is(true));
	}

}
