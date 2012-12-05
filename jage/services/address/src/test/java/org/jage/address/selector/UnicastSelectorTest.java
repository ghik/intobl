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
 * $Id: UnicastSelectorTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.DefaultAgentAddress;
import org.jage.address.node.NodeAddress;
import org.jage.address.selector.agent.AgentAddressSelector;

/**
 * Tests for the UnicastSelector class.
 *
 * @author AGH AgE Team
 */
public class UnicastSelectorTest extends AbstractAddressSelectorTest {

	private AgentAddress address;

	private UnicastSelector<AgentAddress> selector;

	private Collection<AgentAddress> allAddresses;

	private Collection<AgentAddress> usedAddresses;

	private final NodeAddress nodeAddress = mock(NodeAddress.class);

	@Before
	public void setUp() {
		address = new DefaultAgentAddress(nodeAddress);
		allAddresses = new HashSet<AgentAddress>();
		allAddresses.add(address);
		// used addresses is empty
		usedAddresses = new HashSet<AgentAddress>();
		selector = new UnicastSelector<AgentAddress>(address);
	}

	@Override
	protected IAddressSelector<AgentAddress> getSelectorUnderTest() {
		return selector;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAddress() {
		new UnicastSelector<AgentAddress>(null);
	}

	@Test
	public void testNullInitialize() {
		selector.initialize(null, null);
		assertEquals(Collections.singleton(address), collectionFromIterable(selector.addresses()));
	}

	@Test
	public void testEmptySetsInitialize() {
		selector.initialize(new HashSet<AgentAddress>(), new HashSet<AgentAddress>());
		assertEquals(Collections.singleton(address), collectionFromIterable(selector.addresses()));
	}

	@Test
	public void testEmptyUsedSetInitialize() {
		selector.initialize(allAddresses, usedAddresses);
		assertEquals(Collections.singleton(address), collectionFromIterable(selector.addresses()));
	}

	@Test
	public void testInconsistentSetInitialize() {
		usedAddresses.add(new DefaultAgentAddress(nodeAddress));
		selector.initialize(allAddresses, usedAddresses);
		assertEquals(Collections.singleton(address), collectionFromIterable(selector.addresses()));
	}

	/**
	 * Tests results of {@link UnicastSelector.addresses} method before and after initialization.
	 */
	@Test
	public void testAddresses() {
		assertEquals(Collections.singleton(address), collectionFromIterable(selector.addresses()));
	}

	@Override
	@Test
	public void testSelectedNull() {
		assertFalse(selector.selected(null));
	}

	@Override
	@Test
	public void testSelectedDifferent() {
		assertFalse(selector.selected(new DefaultAgentAddress(nodeAddress, "some")));
		// the same user friendly name but different address uid
		assertFalse(selector.selected(new DefaultAgentAddress(nodeAddress, "some")));
	}

	@Test
	public void testSelectedOK() {
		assertTrue(selector.selected(address));
	}

	@Test
	public void dummyCoverageTest() {
		assertTrue(new AgentAddressSelector(address).selected(address));
	}

	@Override
	@Test
	public void testInitializeBothNull() {
		// override, but do not throw exception
		super.testInitializeBothNull();
	}

	@Override
	@Test
	public void testInitializeWithInconsistentSets() {
		// override, but do not throw exception
		super.testInitializeWithInconsistentSets();
	}

	@Override
	@Test
	public void testInitializeWithNullAll() {
		// override, but do not throw exception
		super.testInitializeWithNullAll();
	}

	@Override
	@Test
	public void testInitializeWithNullUsed() {
		// override, but do not throw exception
		super.testInitializeWithNullUsed();
	}

}
