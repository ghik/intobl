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
 * Created: 2009-03-12
 * $Id: BroadUsedSelectorTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import org.jage.address.agent.AgentAddress;

/**
 * Tests for {@link BroadUsedSelector} class.
 * 
 * @author AGH AgE Team
 */
public class BroadUsedSelectorTest extends AbstractBroadcastSelectorTest {

	private BroadUsedSelector<AgentAddress> selector;

	@Before
	public void setUp() {
		selector = new BroadUsedSelector<AgentAddress>();
	}

	@Override
	protected IAddressSelector<AgentAddress> getSelectorUnderTest() {
		return selector;
	}

	/*---------------------------------------------------------------------
	 *			Main stream
	 *---------------------------------------------------------------------*/
	@Test
	public void testAddresses() {
		testAddresses(used);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAddressesWithEmptyUsed() {
		testAddresses(all, Collections.EMPTY_SET, Collections.EMPTY_SET);
	}

	@Test
	public void testSelected() {
		testSelected(used);
	}

	/*---------------------------------------------------------------------
	 *			Other cases
	 *---------------------------------------------------------------------*/

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testInitializeWithNullAll() {
		selector.initialize(null, used);
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testInitializeWithNullUsed() {
		selector.initialize(all, null);
	}

	/**
	 * Checks the case when used addresses set contains at least one address which is not in all available addresses
	 * set.
	 */
	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testInitializeWithInconsistentSets() {
		Collection<AgentAddress> toRemove = new HashSet<AgentAddress>();
		for (AgentAddress addr : all) {
			if (used.contains(addr)) {
				toRemove.add(addr);
			}
		}
		all.removeAll(toRemove);
		selector.initialize(all, used);
	}
}
