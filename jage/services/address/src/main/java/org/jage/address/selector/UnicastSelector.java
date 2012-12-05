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
 * Created: 2009-03-09
 * $Id: UnicastSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;
import java.util.Collections;

import org.jage.address.Address;

/**
 * The most simple address selector. Always returns the address given in {@link #UnicastSelector(Address)}.
 * Initialization of this selector doesn't change its state.
 * 
 * @param <AddressClass>
 *            type of an address which can be selected by this selector
 * @author AGH AgE Team
 */
public class UnicastSelector<AddressClass extends Address> implements IAddressSelector<AddressClass> {

	private static final long serialVersionUID = 5814016849899074368L;

	private AddressClass address;

	/**
	 * The only constructor. Wraps the provided address inside a {@link UnicastSelector}.
	 * 
	 * @param address
	 *            address to select
	 */
	public UnicastSelector(AddressClass address) {
		if (null == address) {
			throw new IllegalArgumentException();
		}
		this.address = address;
	}

	@Override
	public Iterable<AddressClass> addresses() {
		return Collections.singleton(address);
	}

	@Override
	public void initialize(Collection<AddressClass> allAddresses, Collection<AddressClass> usedAddresses) {
		// do nothing
	}

	@Override
	public boolean selected(AddressClass address) {
		return this.address.equals(address);
	}
}
