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
 * Created: 2010-03-11
 * $Id: AbstractBroadcastSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;
import java.util.Collections;

import org.jage.address.Address;

/**
 * An abstract class for a broadcast address selectors. All broadcast selectors choose all addresses from a specified
 * set of addresses created on base of collection of all available and unused addresses.
 * 
 * <p>
 * Before initialization, {@link #addresses()} returns no addresses. After initialization it iterates
 * over a list of all addresses from a chosen set (e.g. all unused addresses, all available addresses, etc.)
 * 
 * <p>
 * Multiple calls to {@link #initialize(Collection, Collection)} do not change the chosen addresses.
 * 
 * @param <AddressClass>
 *            type of address which can be selected by this selector
 * 
 * @author AGH AgE Team
 * 
 */
public abstract class AbstractBroadcastSelector<AddressClass extends Address> implements
        IAddressSelector<AddressClass> {

	private static final long serialVersionUID = 1568562272465487568L;

	/**
	 * Collection of selected addresses.
	 */
	protected Collection<AddressClass> resultAddresses;

	@Override
	public Iterable<AddressClass> addresses() {
		if (resultAddresses == null) {
			return Collections.EMPTY_SET;
		}
		return resultAddresses;
	}

	@Override
	public boolean selected(AddressClass address) {
		if (address == null) {
			return false;
		} else if (resultAddresses == null) {
			return true;
		} else {
			return resultAddresses.contains(address);
		}
	}

}
