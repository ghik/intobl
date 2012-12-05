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
 * Created: 2011-07-27
 * $Id: Header.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.message;

import org.jage.address.Address;
import org.jage.address.selector.IAddressSelector;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides a default implementation of {@link IHeader}.
 * 
 * @param <A>
 *            A type of sender and receiver address.
 * @author AGH AgE Team
 */
public class Header<A extends Address> implements IHeader<A> {

	private static final long serialVersionUID = 1L;

	private final IAddressSelector<A> receiverSelector;

	private final A senderAddress;

	/**
	 * Constructs a new header specifying a sender and a selector for receivers.
	 * 
	 * @param senderAddress
	 *            An address of the sender of the message.
	 * @param receiverSelector
	 *            A selector for selecting receivers.
	 */
	public Header(final A senderAddress, final IAddressSelector<A> receiverSelector) {
		this.senderAddress = checkNotNull(senderAddress);
		this.receiverSelector = checkNotNull(receiverSelector);
	}
	
	/**
	 * Constructs a new header specifying a sender and a selector for receivers.
	 * 
	 * @param senderAddress
	 *            An address of the sender of the message.
	 * @param receiverSelector
	 *            A selector for selecting receivers.
	 * @return a new header.
	 */
	public static <V extends Address> Header<V> create(final V senderAddress, final IAddressSelector<V> receiverSelector) {
		return new Header<V>(senderAddress, receiverSelector);
	}

	@Override
	public A getSenderAddress() {
		return senderAddress;
	}

	@Override
	public IAddressSelector<A> getReceiverSelector() {
		return receiverSelector;
	}

	@Override
	public String toString() {
		return String.format("Message header[sender=%s, receivers=%s]", getSenderAddress(), getReceiverSelector());
	}
}
