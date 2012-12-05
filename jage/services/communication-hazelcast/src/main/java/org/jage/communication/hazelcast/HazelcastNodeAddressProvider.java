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
 * $Id: HazelcastNodeAddressProvider.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast;

import org.jage.address.node.NodeAddress;
import org.jage.address.node.NodeAddressSupplier;

import com.hazelcast.core.HazelcastInstance;

/**
 * The implementation of the node address provider that uses Hazelcast UUIDs.
 *
 * @see HazelcastNodeAddress
 *
 * @author AGH AgE Team
 */
public class HazelcastNodeAddressProvider implements NodeAddressSupplier {

	private HazelcastNodeAddress nodeAddress;

	@Override
	public NodeAddress get() {
		if (nodeAddress == null) {
			final HazelcastInstance hazelcastInstance = HazelcastInstanceFactory.getInstance();
			nodeAddress = new HazelcastNodeAddress(hazelcastInstance.getCluster().getLocalMember().getUuid());
		}
		return nodeAddress;
	}
}
