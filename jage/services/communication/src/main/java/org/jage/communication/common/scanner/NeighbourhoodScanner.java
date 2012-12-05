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
 * $Id: NeighbourhoodScanner.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.scanner;

import org.jage.communication.common.cache.AddressCache;
import org.jage.communication.common.cache.AddressMap;
import org.jage.communication.common.cache.AddressSet;
import org.jage.platform.component.IStatefulComponent;

/**
 * A neighbourhood scanner of the node.
 * <p>
 * A task of the scanner is to obtain addresses necessary for the local node to communicate with its neighbours. A
 * necessary address may be a AgE address, a physical one (like an IP address) or both.
 * <p>
 * Scanner is expected to fill an {@link AddressCache} with all addresses it finds. It should keep the cache constantly
 * up-to-date.
 * <p>
 * Some scanners will require an {@link AddressMap} (a map) and others will use an {@link AddressSet} (a set).
 * 
 * @author AGH AgE Team
 */
public interface NeighbourhoodScanner extends IStatefulComponent {

	/**
	 * Initializes the neighbourhood scanner.
	 */
	@Override
	void init();

	/**
	 * Will stop the neighbourhood scanner, possibly with a short delay.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	boolean finish();

	/**
	 * Checks whether the scanner is currently starting.
	 * 
	 * @return true if the scanner is starting, false otherwise.
	 */
	boolean isStarting();

	/**
	 * Checks whether the scanner is currently alive.
	 * 
	 * @return true if the scanner is alive, false otherwise.
	 */
	boolean isAlive();

	/**
	 * Checks whether the scanner is currently stopped.
	 * 
	 * @return true if the scanner is stopped, false otherwise.
	 */
	boolean isStopped();
}
