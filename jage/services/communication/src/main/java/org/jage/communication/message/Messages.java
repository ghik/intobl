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
 * Created: 2012-02-08
 * $Id: Messages.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.message;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.lang.String.format;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jage.address.Address;
import org.jage.address.component.DefaultComponentAddress;
import org.jage.address.component.ComponentAddress;
import org.jage.address.selector.BroadcastSelector;
import org.jage.address.selector.IAddressSelector;

import static org.jage.address.selector.Selectors.isMultiTarget;
import static org.jage.address.selector.Selectors.unicastFor;

import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Message-related utilities.
 * 
 * @author AGH AgE Team
 */
public final class Messages {

	private Messages() {
	}

	/**
	 * Returns the address of the only receiver selected by the selector in the provided message.
	 * 
	 * @param message
	 *            a message to extract receiver from.
	 * @param <A>
	 *            a type of addresses.
	 * @return An extracted receiver.
	 * 
	 * @throws NoSuchElementException
	 *             if there is no selected receiver.
	 * @throws IllegalArgumentException
	 *             if the selector selects multiple addresses.
	 */
	@Nonnull
	public static <A extends Address> A getOnlyReceiverAddress(final IMessage<A, ?> message) {
		checkNotNull(message);
		return Iterables.getOnlyElement(message.getHeader().getReceiverSelector().addresses());
	}

	/**
	 * Returns the address of the sender of the provided message.
	 * 
	 * @param message
	 *            a message to extract receiver from.
	 * @param <A>
	 *            a type of addresses.
	 * @return An extracted sender.
	 */
	@Nonnull
	public static <A extends Address> A getSenderAddress(final IMessage<A, ?> message) {
		checkNotNull(message);
		return message.getHeader().getSenderAddress();
	}

	/**
	 * Returns the payload carried by a message if it is of a given type. Otherwise, it throws an exception.
	 * 
	 * @param message
	 *            a message.
	 * @param klass
	 *            a supposed class of the payload.
	 * @param <T>
	 *            a type of the payload.
	 * @return the message payload.
	 * @throws IllegalArgumentException
	 *             if the payload is of incorrect type.
	 */
	public static <T extends Serializable> T getPayloadOfTypeOrThrow(
	        final IMessage<? extends Address, ? super T> message, final Class<T> klass) {
		checkNotNull(message);
		checkNotNull(klass);

		final Serializable payload = message.getPayload();

		checkNotNull(payload, "Message cannot have a null payload.");

		checkArgument(
		        klass.isAssignableFrom(payload.getClass()),
		        format("Message payload has incorrect type. %s was expected but %s was received.", klass.getClass(),
		                payload.getClass()));
		return klass.cast(payload);
	}

	/**
	 * Creates a new message that selects all components with the same name as provided from the distributed
	 * environment.
	 * 
	 * @param senderAddress
	 *            the address of the sender.
	 * @param payload
	 *            the payload.
	 * @param <A>
	 *            a type of the address.
	 * @param <P>
	 *            a type of the payload.
	 * @return a new message.
	 */
	public static <A extends Address, P extends Serializable> IMessage<A, P> newBroadcastMessage(
	        final A senderAddress, final P payload) {
		final IHeader<A> header = new Header<A>(senderAddress, new BroadcastSelector<A>());
		return new Message<A, P>(header, payload);
	}

	/**
	 * Creates a new unicast message.
	 * 
	 * @param from
	 *            the address of the sender.
	 * @param to
	 *            the address of the receiver.
	 * @param payload
	 *            the payload.
	 * @param <A>
	 *            a type of the address.
	 * @param <P>
	 *            a type of the payload.
	 * @return a new message.
	 */
	public static <A extends Address, P extends Serializable> IMessage<A, P> newUnicastMessage(final A from,
	        final A to, @Nullable final P payload) {
		final IHeader<A> header = new Header<A>(from, unicastFor(to));
		return new Message<A, P>(header, payload);
	}

	/**
	 * Converts a multitarget message (e.g. broadcast) to set of unicast messages.
	 * 
	 * @param message
	 *            a message to convert. It must have a multitarget and initialized selector.
	 * @return a set of unicast messages.
	 */
	public static Set<IMessage<ComponentAddress, Serializable>> multitargetToUnicast(final IMessage<?, ?> message) {

		final IHeader<ComponentAddress> header = (IHeader<ComponentAddress>)message.getHeader();
		final IAddressSelector<ComponentAddress> selector = header.getReceiverSelector();
		checkArgument(isMultiTarget(selector), "Message must have a multi-target selector.");
		// XXX: There is currently no way to check, whether selector is initialised

		// Multi-target addresses do not carry target component identification, we use the sender component name
		final String senderComponentName = header.getSenderAddress().getIdentifier();
		checkArgument(senderComponentName != null, "Unknown a component name of the sender.");

		final Set<IMessage<ComponentAddress, Serializable>> messages = newHashSet();
		for (final ComponentAddress current : selector.addresses()) {
			// Selectors are stupid and because of it we need to create new addresses for unicast selectors based
			// on the name of the sender
			final ComponentAddress corrected = new DefaultComponentAddress(senderComponentName, current.getNodeAddress());
			final IMessage<ComponentAddress, Serializable> newMessage = newUnicastMessage(header.getSenderAddress(),
			        corrected, (Serializable)message.getPayload());

			messages.add(newMessage);
		}
		return messages;
	}
}
