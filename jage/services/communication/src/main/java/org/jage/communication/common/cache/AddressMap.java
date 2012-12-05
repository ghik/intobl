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
 * $Id: AddressMap.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.cache;

import javax.annotation.CheckForNull;

import org.jage.address.node.NodeAddress;

/**
 * A type of address cache that maps node addresses to physical ones.
 * <p>
 * Remember that every environment with an AddressMap requires also an {@link PhysicalAddressProvider}.
 * 
 * @author AGH AgE Team
 */
public interface AddressMap extends MutableAddressCache {

	/**
	 * Adds a mapping for a node address to the database.
	 * 
	 * @param nodeAddress
	 *            a node's address to add.
	 * @param physicalAddress
	 *            a physical address that maps to the given node address.
	 */
	void addAddressMapping(NodeAddress nodeAddress, String physicalAddress);

	/**
	 * Returns the physical (protocol-dependent) address of the node represented by the given address.
	 * 
	 * @param nodeAddress
	 *            an address of node.
	 * @return a physical address corresponding to the given node's address. May be {@code null}.
	 */
	@CheckForNull
	String getPhysicalAddressFor(NodeAddress nodeAddress);

}
