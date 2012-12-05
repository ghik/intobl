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
 * Created: 2012-02-07
 * $Id: AddressMapTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jage.address.node.NodeAddress;
import org.jage.platform.time.ManualTicker;

import com.google.common.base.Ticker;
import com.google.common.collect.Lists;

/**
 * Tests for the {@link DefaultAddressMap} class.
 * 
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressMapTest {

	private DefaultAddressMap addressMap;

	private final ManualTicker ticker = new ManualTicker();

	@Before
	public void setup() {
		addressMap = new DefaultAddressMap(1, ticker);
	}

	@Test
	public void testAddAddressMapping() {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);
		String physicalAddress = "physicalAddress";

		// when
		addressMap.addAddressMapping(nodeAddress, physicalAddress);

		// then
		assertThat(addressMap.getPhysicalAddressFor(nodeAddress), equalTo(physicalAddress));
	}

	@Test
	public void testRemoveAddress() {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);
		String physicalAddress = "physicalAddress";

		// when
		addressMap.addAddressMapping(nodeAddress, physicalAddress);
		addressMap.removeAddress(nodeAddress);

		// then
		assertThat(addressMap.getPhysicalAddressFor(nodeAddress), is(nullValue()));
	}

	@Test
	public void testRemoveAllAddresses() {
		// given
		List<NodeAddress> listOfAddresses = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			NodeAddress nodeAddress = mock(NodeAddress.class);
			listOfAddresses.add(nodeAddress);
			addressMap.addAddressMapping(nodeAddress, "physicalAddress" + i);
		}

		// when
		addressMap.removeAllAddresses();

		// then
		for (NodeAddress nodeAddress : listOfAddresses) {
			assertThat(addressMap.getPhysicalAddressFor(nodeAddress), is(nullValue()));
		}
	}

	@Test
	public void testCacheModificationListeners() {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);
		String physicalAddress = "physicalAddress";
		CacheModificationListener cacheModificationListener = mock(CacheModificationListener.class);

		// when
		addressMap.addCacheModificationListener(cacheModificationListener);
		addressMap.addAddressMapping(nodeAddress, physicalAddress);
		addressMap.removeAddress(nodeAddress);
		addressMap.removeAllAddresses();

		// then
		verify(cacheModificationListener, times(3)).onCacheChanged();
	}

	@Test(timeout = 4000)
	public void testTimeoutingEntries() throws InterruptedException {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);
		String physicalAddress = "physicalAddress";
		CacheModificationListener cacheModificationListener = mock(CacheModificationListener.class);

		// when
		addressMap.addCacheModificationListener(cacheModificationListener);
		addressMap.addAddressMapping(nodeAddress, physicalAddress);

		// Make time flow faster
		ticker.increaseValue(TimeUnit.SECONDS.toNanos(2));

		// then
		verify(cacheModificationListener, atLeastOnce()).onCacheChanged();
		assertThat(addressMap.getAddressesOfNeighbours().size(), equalTo(0));
	}
}
