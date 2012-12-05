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
 * Created: 2009-04-21
 * $Id: AnycastSelectorTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import org.jage.address.agent.AgentAddress;

public class AnycastSelectorTest extends AbstractAddressSelectorTest {

	private AnycastSelector<AgentAddress> selector;

	@Before
	public void setUp() {
		selector = new AnycastSelector<AgentAddress>();
		selector.random = random;
	}

	@Test
	public void testAddresses() {
		testAddressesOneOf(all);
	}

	@Test
	public void testSelected() {
		testSelectedOneOf(all);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSelectedWithEmptyUsedSet() {
		testSelectedOneOf(all, Collections.EMPTY_SET, all);
	}

	/**
	 * Overridden method because anycast should ignore used set, therefore no exception should be thrown.
	 */
	@Test
	@Override
	public void testInitializeWithNullUsed() {
		super.testInitializeWithNullUsed();
	}

	/**
	 * Overridden method because broadcast should ignore used set, therefore no exception should be thrown.
	 */
	@Test
	@Override
	public void testInitializeWithInconsistentSets() {
		super.testInitializeWithInconsistentSets();
	}

	@Override
	protected IAddressSelector<AgentAddress> getSelectorUnderTest() {
		return selector;
	}

}
