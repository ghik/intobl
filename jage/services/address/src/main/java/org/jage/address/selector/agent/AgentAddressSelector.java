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
 * Created: 2009-03-11
 * $Id: AgentAddressSelector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector.agent;

import org.jage.address.agent.AgentAddress;
import org.jage.address.selector.UnicastSelector;

/**
 * A {@link UnicastSelector} for an {@link AgentAddress}. Present for convenience only.
 * 
 * @author AGH AgE Team
 */
public final class AgentAddressSelector extends UnicastSelector<AgentAddress> {

	private static final long serialVersionUID = -4278275788478772206L;

	/**
	 * Default constructor.
	 * 
	 * @param address
	 *            agent address to be selected
	 */
	public AgentAddressSelector(AgentAddress address) {
		super(address);
	}
}
