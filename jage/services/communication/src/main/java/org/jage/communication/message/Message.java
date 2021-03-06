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
 * $Id: Message.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.message;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.jage.address.Address;

import com.google.common.base.Objects.ToStringHelper;

import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides a default implementation of {@link IMessage}.
 *
 * @param <A>
 * 		An address type of a sender and a receiver
 * @param <T>
 * 		A type of the payload transported within this message.
 * @author AGH AgE Team
 */
public class Message<A extends Address, T extends Serializable> implements IMessage<A, T> {

	private static final long serialVersionUID = 2037459335733049533L;

	private final IHeader<A> header;

	@Nullable
	private final T payload;

	/**
	 * Constructs a new message with the given header and the payload.
	 *
	 * @param header
	 * 		A header that contains metadata for this message.
	 * @param payload
	 * 		A payload to transport.
	 */
	public Message(final IHeader<A> header, @Nullable final T payload) {
		this.header = checkNotNull(header);
		this.payload = payload;
	}

	/**
	 * Constructs a new message with the given header and with no payload.
	 *
	 * @param header
	 * 		A header that contains metadata for this message.
	 */
	public Message(final IHeader<A> header) {
		this(header, null);
	}

	/**
	 * Constructs a new message with the given header and the payload.
	 *
	 * @param header
	 * 		A header that contains metadata for this message.
	 * @param payload
	 * 		A payload to transport.
	 * @return a new message.
	 */
	public static <V extends Address, Z extends Serializable> Message<V, Z> create(final IHeader<V> header,
			@Nullable final Z payload) {
		return new Message<V, Z>(header, payload);
	}

	/**
	 * Constructs a new message with the given header and with no payload.
	 *
	 * @param header
	 * 		A header that contains metadata for this message.
	 * @return a new message.
	 */
	public static <V extends Address, Z extends Serializable> Message<V, Z> create(final IHeader<V> header) {
		return new Message<V, Z>(header);
	}

	@Override
	public IHeader<A> getHeader() {
		return header;
	}

	@Override
	@CheckForNull
	public T getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		final ToStringHelper helper = toStringHelper(this).add("header", getHeader());
		if (payload != null) {
			helper.add("payload-class", payload.getClass().getSimpleName());
		}
		return helper.toString();
	}
}
