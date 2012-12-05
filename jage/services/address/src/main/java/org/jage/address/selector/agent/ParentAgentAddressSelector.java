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
 * $Id: ParentAgentAddressSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector.agent;

import java.util.Collection;
import java.util.Collections;

import org.jage.address.agent.AgentAddress;
import org.jage.address.selector.IAddressSelector;

/**
 * This class provides the address selector used for choosing a parent agent.
 * <p>
 * This selector works a little not intuitively. It does not select any addresses, it is an aggregate that needs to check
 * if it is selected. An aggregate is selected if an address returned by {@link #addresses} method is one of its children
 * addresses.
 * 
 * @author AGH AgE Team
 */
public class ParentAgentAddressSelector implements IAddressSelector<AgentAddress> {

	private static final long serialVersionUID = -720158404815435693L;

	private AgentAddress childAddress;

	/**
	 * Constructs a new selector that selects a parent of the child with the provided address.
	 * 
	 * @param childAddress
	 *            An address of <b>the child</b>.
	 */
	public ParentAgentAddressSelector(AgentAddress childAddress) {
		this.childAddress = childAddress;
	}

	public AgentAddress getChildAddress() {
		return childAddress;
	}

	@Override
	public Iterable<AgentAddress> addresses() {
		return Collections.singleton(childAddress);
	}

	@Override
	public void initialize(Collection<AgentAddress> allAddresses, Collection<AgentAddress> usedAddresses) {
		// Empty
	}

	@Override
	public boolean selected(AgentAddress address) {
		return false;
	}

}
