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
 * $Id: NewAddressSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.jage.address.Address;

/**
 * An address selector that behaves similarly to the {@link UnicastSelector}, but does not return the wrapped address in
 * {@link #addresses()} before it is initialized.
 * 
 * <p>
 * This selector is especially handy when used during agent action execution. An action on a not-yet-existent agent can
 * be queued and executed using this selector, and it will pass action validation because {@link #addresses()} does not
 * return the address until after {@link #initialize(Collection, Collection)} is called for the first time.
 * 
 * @param <AddressClass>
 *            type of an address which can be selected by this selector
 * 
 * @author AGH AgE Team
 */
public class NewAddressSelector<AddressClass extends Address> implements IAddressSelector<AddressClass> {

	private static final long serialVersionUID = 2387652065105445959L;

	private AddressClass address;

	private boolean initialized;

	/**
	 * Constructs a new {@link NewAddressSelector} using the address provided.
	 * 
	 * @param address
	 *            address to wrap inside this selector
	 */
	public NewAddressSelector(AddressClass address) {
		if (null == address) {
			throw new IllegalArgumentException();
		}
		this.address = address;
	}

	@Override
	public Iterable<AddressClass> addresses() {
		return initialized ? Collections.singleton(address) : new HashSet<AddressClass>();
	}

	@Override
	public void initialize(Collection<AddressClass> allAddresses, Collection<AddressClass> usedAddresses) {
		this.initialized = true;
	}

	@Override
	public boolean selected(Address address) {
		return initialized ? this.address.equals(address) : false;
	}

}
