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
 * Created: 2011-08-29
 * $Id: MultiUsedSelectorTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.jage.address.agent.AgentAddress;

/**
 * Tests for {@link MultiUsedSelector} class.
 * 
 * @author AGH AgE Team
 */
public class MultiUsedSelectorTest extends AbstractAddressSelectorTest {

	private MultiUsedSelector<AgentAddress> selector;

	private int addressCount;

	@Before
	public void setUp() {
		addressCount = used.size() / 2;
		selector = new MultiUsedSelector<AgentAddress>(addressCount);
	}

	@Override
	protected IAddressSelector<AgentAddress> getSelectorUnderTest() {
		return selector;
	}

	/**
	 * Tests addresses() method in the standard, correct situation.
	 */
	@Test
	public void testAddresses() {
		assertEquals(Collections.EMPTY_SET, collectionFromIterable(getSelectorUnderTest().addresses()));

		getSelectorUnderTest().initialize(all, used);

		Collection<AgentAddress> selectedAddresses = collectionFromIterable(getSelectorUnderTest().addresses());
		assertEquals(addressCount, selectedAddresses.size());

		for (AgentAddress agentAddress : selectedAddresses) {
			assertTrue(used.contains(agentAddress));
		}
	}

	/**
	 * Tests selected() method in the standard, correct situation.
	 */
	@Test
	public void testSelected() {
		for (AgentAddress addr : all) {
			assertFalse(getSelectorUnderTest().selected(addr));
		}

		getSelectorUnderTest().initialize(all, used);

		int selectedCount = 0;
		for (AgentAddress addr : used) {
			if (getSelectorUnderTest().selected(addr)) {
				selectedCount++;
			}
		}
		assertEquals(addressCount, selectedCount);

		// Test whether any unwanted address was not selected
		for (AgentAddress addr : unused) {
			assertFalse(getSelectorUnderTest().selected(addr));
		}
	}

}
