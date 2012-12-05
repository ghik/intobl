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
 * Created: 2012-07-18
 * $Id: HazelcastNodeAddress.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast;

import org.jage.address.node.AbstractNodeAddress;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A default implementation of a node address.
 *
 * @author AGH AgE Team
 */
public class HazelcastNodeAddress extends AbstractNodeAddress {

	private static final long serialVersionUID = 1L;

	private final String uuid;

	/**
	 * Constructs a new {@code HazelcastNodeAddress} based on a UUID.
	 *
	 * @param uuid
	 *            Hazelcast UUID for the node.
	 *
	 * @throws NullPointerException
	 *             If {@code uuid} is {@code null}.
	 */
	public HazelcastNodeAddress(final String uuid) {
		this.uuid = checkNotNull(uuid);
	}

	@Override
	public String getIdentifier() {
		return uuid;
	}

	@Override
	public String toString() {
		return String.format("Hazelcast[%s]", uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(uuid);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof HazelcastNodeAddress) {
			final HazelcastNodeAddress other = (HazelcastNodeAddress)obj;
			if (Objects.equal(uuid, other.uuid)) {
				return true;
			}
		}
		return false;
	}
}
