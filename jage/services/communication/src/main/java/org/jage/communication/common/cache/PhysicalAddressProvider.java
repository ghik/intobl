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
 * $Id: PhysicalAddressProvider.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.cache;

import org.jage.address.node.NodeAddressSupplier;
import org.jage.communication.common.protocol.CommunicationProtocol;
import org.jage.communication.common.scanner.NeighbourhoodScanner;

/**
 * In every environment that uses an {@link AddressMap}, there should be a PhysicalAddressProvider available. In such an
 * environment, the neighbourhood scanner will need to know its node and physical addresses in order to announce those
 * to other scanners in the neighbourhood.
 * <p>
 * While it can always get the node address from AgE's {@link NodeAddressSupplier}, it might be unable to obtain the
 * physical address because it may be, for example, managed by the {@link CommunicationProtocol}.
 * <p>
 * That's why an {@link NeighbourhoodScanner} will always learn the physical address from a PhysicalAddressProvider.
 * Every environment with a map should have exactly one such provider.
 * 
 * @author AGH AgE Team
 */
public interface PhysicalAddressProvider {

	/**
	 * Returns the physical address (e.g. IP) of the node.
	 * 
	 * @return the local physical communication related address.
	 */
	String getLocalPhysicalAddress();

}
