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
 * $Id: AbstractAnycastSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import org.jage.address.Address;

/**
 * An abstract implementation of an anycast selector. All anycast selectors choose one random address (hence the "any.."
 * part of the name) from provided lists of available and used addresses.
 * 
 * <p>
 * Before initialization, this selector returns no addresses in {@link #addresses()}. After initialization,
 * {@link #addresses()} iterates over a single, randomly chosen address.
 * 
 * <p>
 * Multiple calls to {@link #initialize(Collection, Collection)} do not change the randomly chosen address.
 * 
 * @param <AddressClass>
 *            type of address which can be selected by this selector
 * @author AGH AgE Team
 */
public abstract class AbstractAnycastSelector<AddressClass extends Address> implements IAddressSelector<AddressClass> {

	private static final long serialVersionUID = 6566908067008958990L;

	/**
	 * Selected address.
	 */
	protected AddressClass resultAddress;

	/**
	 * Random generator used by this selector.
	 */
	Random random;

	/**
	 * Default constructor.
	 */
	public AbstractAnycastSelector() {
		random = new Random();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<AddressClass> addresses() {
		if (resultAddress == null) {
			return Collections.EMPTY_SET;
		} else {
			return Collections.singleton(resultAddress);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean selected(AddressClass address) {
		if (resultAddress == null) {
			return false;
		} else {
			return resultAddress.equals(address);
		}
	}

}
