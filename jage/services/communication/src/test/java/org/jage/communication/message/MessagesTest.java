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
 * Created: 2012-03-04
 * $Id: MessagesTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.message;

import java.io.Serializable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.jage.address.Address;
import org.jage.address.component.ComponentAddress;
import org.jage.address.selector.UnicastSelector;

/**
 * Tests for the {@link Messages} class.
 * 
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class MessagesTest {

	@Mock
	private ComponentAddress senderAddress;
	
	@Test
	public void testGetOnlyReceiverAddressWithOneReceiver() {
		// given
		Address receiverAddress = mock(ComponentAddress.class);
		UnicastSelector<Address> selector = new UnicastSelector<Address>(receiverAddress);
		Header<Address> header = new Header<Address>(senderAddress, selector);
		Message<Address, Serializable> message = new Message<Address, Serializable>(header, mock(Serializable.class));
		
		// when
		Address onlyReceiverAddress = Messages.getOnlyReceiverAddress(message);
		
		// then
		assertThat(onlyReceiverAddress, is(equalTo(receiverAddress)));
	}

	@SuppressWarnings("unchecked")
    @Test
	public void testGetPayloadOfTypeOrThrowWithCorrectType() {
		// given
		String samplePayload = "payload"; 
		Message<Address, String> message = new Message<Address, String>(mock(IHeader.class), samplePayload);
		
		// when
		String returnedPayload = Messages.getPayloadOfTypeOrThrow(message, String.class);
		
		// then
		assertThat(returnedPayload, is(equalTo(samplePayload)));
	}
	
	@SuppressWarnings("unchecked")
    @Test(expected=IllegalArgumentException.class)
	public void testGetPayloadOfTypeOrThrowWithWrongType() {
		// given
		String samplePayload = "payload"; 
		Message<Address, Serializable> message = new Message<Address, Serializable>(mock(IHeader.class), samplePayload);
		
		// when
		Messages.getPayloadOfTypeOrThrow(message, Integer.class);
		
		// then should throw
	}

}
