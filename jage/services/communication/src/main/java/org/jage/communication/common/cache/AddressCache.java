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
 * $Id: AddressCache.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.cache;

import java.util.Collection;

import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddress;
import org.jage.communication.common.manager.CommunicationManager;
import org.jage.communication.common.scanner.NeighbourhoodScanner;

/**
 * A model object containing node addresses of local node's neighbours.
 * <p>
 * It's a mediator standing between an {@link NeighbourhoodScanner} and an {@link CommunicationManager}. Scanner fills
 * the AddressCache with addresses which a service will use each time it is initializing a selector.
 * <p>
 * It's assumed that there's only one NeighbourhoodScanner per node and that the scanner is the only component who's
 * filling the cache with addresses.
 * <p>
 * Sends events to interested listeners each time the underlying database changes.
 * 
 * @see NeighbourhoodScanner
 * 
 * @author AGH AgE Team
 */
public interface AddressCache {

	/**
	 * Returns all addresses in the cache but represented as component addresses of communication services for nodes.
	 * 
	 * @return addresses of all the known nodes; be aware that the component id included in each of those can make no
	 *         sense and you should provide your own if necessary.
	 */
	Collection<ComponentAddress> getAddressesOfNeighbours();
	
	Collection<NodeAddress> getAddressesOfAllNodes();

	/**
	 * Adds a listener which will be notified each time the underlying database of addresses changes.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	void addCacheModificationListener(CacheModificationListener listener);

	/**
	 * Returns a count of currently known foreign nodes.
	 * 
	 * @return a count of currently known foreign nodes.
	 */
	long getNodesCount();

}
