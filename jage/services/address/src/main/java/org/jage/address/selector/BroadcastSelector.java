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
 * $Id: BroadcastSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;

import org.jage.address.Address;

import static com.google.common.base.Objects.toStringHelper;

import javax.annotation.Nonnull;

/**
 * The simplest broadcast selector, which selects all available addresses. May be used e.g. for broadcasting messages.
 * 
 * @param <AddressClass>
 *            type of address which can be selected by this selector
 * 
 * @author AGH AgE Team
 */
public class BroadcastSelector<AddressClass extends Address> extends AbstractBroadcastSelector<AddressClass> {

	private static final long serialVersionUID = 8005515047272772268L;
	
	/**
	 * Creates a new {@code BroadcastSelector}.
	 * 
	 * @return a new selector.
	 */
	@Nonnull
	public static <T extends Address> BroadcastSelector<T> create() {
		return new BroadcastSelector<T>();
	}

	@Override
	public void initialize(Collection<AddressClass> allAddresses, Collection<AddressClass> usedAddresses) {
		if (allAddresses == null) {
			throw new IllegalArgumentException("Cannot initialize BroadcastSelector with null all addresses set");
		}

		this.resultAddresses = allAddresses;
	}

	@Override
	public String toString() {
		return toStringHelper(this).toString();
	}
}
