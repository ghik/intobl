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
 * $Id: IAddressSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.io.Serializable;
import java.util.Collection;

import org.jage.address.Address;

/**
 * An address selector which selects one or more addresses according to some internal rules. This interface was
 * originally intended for setting targets on agent actions, but may be used widely in the future.
 * 
 * <p>
 * All selectors can have two states - uninitialized and initialized. The transition is made by calling the
 * {@link #initialize(Collection, Collection)} method. This method may be called multiple times and may not change the
 * state of the selector upon subsequent calls. For instance, for selectors that select a random address, the chosen
 * address must not change upon subsequent calls to this method.
 * 
 * <p>
 * An address selector introduces the concept of "used" addresses. A used address is an address returned by the
 * {@link #addresses()} class while the selector is still in an uninitialized state. There may exist selectors which
 * choose addresses based on their "used" status - regardless of the actual meaning of this status. For further
 * information, cf. agent action invocation.
 * 
 * @param <AddressClass>
 *            type of address which can be selected by this selector
 * 
 * @author AGH AgE Team
 */
public interface IAddressSelector<AddressClass extends Address> extends Serializable {

	/**
	 * Retrieve addresses chosen by this selector.
	 * 
	 * Before {@link #initialize(Collection, Collection)} is called, this method returns an iterable over "used"
	 * addresses (cf. class comment). After {@link #initialize(Collection, Collection)} is called, this method returns
	 * an iterable over addresses chosen by this selector.
	 * 
	 * @return an {@link Iterable} of the addresses chosen by this selector.
	 */
	Iterable<AddressClass> addresses();

	/**
	 * Check if an address is selected by this selector. This method should return true if and only if iterating over
	 * {@link #addresses()} would contain <code>address</code>. The same restrictions with respect to
	 * {@link #initialize(Collection, Collection)} apply as described in {@link #addresses()}.
	 * 
	 * @param address
	 *            an address to be checked
	 * @return true if this selector selects <code>address</code>; false otherwise
	 */
	boolean selected(AddressClass address);

	/**
	 * Initialize this selector. This method may be called any number of times, and should change this selector's state
	 * from uninitialized to initialized only once.
	 * 
	 * For example, for selectors that select a random address, this method may be responsible for making the random
	 * choice of an address.
	 * 
	 * @param allAddresses
	 *            all addresses available to be selected from
	 * @param usedAddresses
	 *            a list of "used" addresses, which may be interpreted by this selector somehow in the process of
	 *            selecting its address(-es)
	 */
	void initialize(Collection<AddressClass> allAddresses, Collection<AddressClass> usedAddresses);
}
