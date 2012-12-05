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
 * Created: 2009-03-10
 * $Id: AnyUnusedSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;

import org.jage.address.Address;

/**
 * An anycast selector that chooses a random address from the poll of unused addresses.
 * 
 * @param <AddressClass>
 *            type of address which can be selected by this selector
 * 
 * @author AGH AgE Team
 */
public class AnyUnusedSelector<AddressClass extends Address> extends AbstractAnycastSelector<AddressClass> {

	private static final long serialVersionUID = 5919859890040844079L;

	@Override
	public void initialize(Collection<AddressClass> allAddresses, Collection<AddressClass> usedAddresses) {

		if (resultAddress != null) {
			return; // selector is already initialized
		}
		if (allAddresses == null || usedAddresses == null) {
			throw new IllegalArgumentException("Cannot initialize selector with null sets of addresses");
		}

		Collection<AddressClass> unusedAddresses = SelectorHelper.createUnusedSet(allAddresses, usedAddresses);

		if (unusedAddresses.isEmpty()) {
			throw new IllegalArgumentException("Cannot initialize selector with empty set of unused addresses");
		}

		// generate random index
		int elem = random.nextInt(unusedAddresses.size());

		int i = 0;
		for (AddressClass addr : unusedAddresses) {
			if (i++ == elem) {
				resultAddress = addr;
				break;
			}
		}
	}

}
