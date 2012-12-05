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
 * Created: 2011-07-19
 * $Id: SelectorHelper.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;
import java.util.HashSet;

import org.jage.address.Address;

/**
 * A utility class for address selectors.
 * 
 * @author AGH AgE Team
 * 
 */
public class SelectorHelper {

	/**
	 * Checks if all addresses from <code>usedAddresses</code> collection exists in <code>allAddresses</code>. If not, a
	 * new {@link IllegalArgumentException} is thrown.
	 * 
	 * @param <AddressClass>
	 *            address class
	 * @param allAddresses
	 *            all addresses
	 * @param usedAddresses
	 *            used addresses
	 */
	public static <AddressClass extends Address> void checkSetsInconsistence(Collection<AddressClass> allAddresses,
	        Collection<AddressClass> usedAddresses) {

		// check if sets are inconsistent
		for (AddressClass addr : usedAddresses) {
			if (!allAddresses.contains(addr)) {
				throw new IllegalArgumentException("The given sets are inconsistent. Address " + addr.toString()
				        + " does not exists in all addresses set but it is in used addresses set.");
			}
		}
	}

	/**
	 * Creates set of unused addresses (these which are in <code>allAddresses</code> collection and are not in
	 * <code>usedAddresses</code> collection). This methods checks also if all addresses from <code>usedAddresses</code>
	 * collection exists in <code>allAddresses</code>. If not, a new {@link IllegalArgumentException} is thrown.
	 * 
	 * @param <AddressClass>
	 *            address class
	 * @param allAddresses
	 *            all addresses
	 * @param usedAddresses
	 *            used addresses
	 * @return collection which contains unused addresses
	 */
	public static <AddressClass extends Address> Collection<AddressClass> createUnusedSet(
	        Collection<AddressClass> allAddresses, Collection<AddressClass> usedAddresses) {
		// unused addresses set is a HashSet instance
		Collection<AddressClass> result = new HashSet<AddressClass>();
		result.addAll(allAddresses);

		for (AddressClass addr : usedAddresses) {
			if (!result.contains(addr)) {
				throw new IllegalArgumentException("The given sets are inconsistent. Address " + addr.toString()
				        + " does not exists in all addresses set but it is in used addresses set.");
			} else {
				result.remove(addr);
			}
		}
		return result;
	}
}
