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
 * Created: 2012-04-07
 * $Id: Selectors.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import javax.annotation.Nonnull;

import org.jage.address.Address;
import org.jage.address.agent.AgentAddress;
import org.jage.address.selector.agent.ParentAgentAddressSelector;

import static com.google.common.collect.Iterables.getOnlyElement;

/**
 * Utility class for selectors.
 * 
 * @author AGH AgE Team
 */
public final class Selectors {

	/**
	 * Creates a unicast selector for the given agent address.
	 * 
	 * @param address
	 *            some agent address.
	 * @return a unicast selector for the given address.
	 */
	@Nonnull
	public static <T extends Address> UnicastSelector<T> unicastFor(final T address) {
		return new UnicastSelector<T>(address);
	}

	/**
	 * Creates a parent-agent selector for the given agent address.
	 * 
	 * @param childAddress
	 *            an address of the child.
	 * @return a parent-agent selector for the given address
	 */
	@Nonnull
	public static ParentAgentAddressSelector parentOf(final AgentAddress childAddress) {
		return new ParentAgentAddressSelector(childAddress);
	}

	/**
	 * Returns the only address selected by a unicast selector.
	 * 
	 * @param selector
	 *            a unicast selector.
	 * @return the only address selected.
	 */
	public static <T extends Address> T getOnlyAddress(final UnicastSelector<T> selector) {
		return getOnlyElement(selector.addresses());
	}

	/**
	 * Tells whether a provided selector may select multiple targets.
	 * 
	 * @param selector
	 *            a selector.
	 * @return true if the selector may select multiple targets, false otherwise.
	 */
	@Nonnull
	public static boolean isMultiTarget(final IAddressSelector<?> selector) {
		return selector instanceof AbstractBroadcastSelector<?> || selector instanceof AbstractMulticastSelector<?>
		        || selector instanceof AnycastSelector<?>;
	}

	private Selectors() {
	}
}
