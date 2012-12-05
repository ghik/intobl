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
 * $Id: AddressSetTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.jage.address.node.NodeAddress;
import org.jage.platform.time.ManualTicker;

import com.google.common.collect.Lists;

/**
 * Tests for the {@link DefaultAddressSet} class.
 * 
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressSetTest {

	private DefaultAddressSet addressSet;

	private final ManualTicker ticker = new ManualTicker();

	@Before
	public void setup() {
		addressSet = new DefaultAddressSet(1, ticker);
	}

	@Test
	public void testAddAddress() {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);

		// when
		addressSet.addAddress(nodeAddress);

		// then
		assertThat(addressSet.getAddressesOfNeighbours().size(), equalTo(1));
	}

	@Test
	public void testAddAddressBehavesLikeSet() {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);

		// when
		addressSet.addAddress(nodeAddress);
		addressSet.addAddress(nodeAddress);

		// then
		assertThat(addressSet.getAddressesOfNeighbours().size(), equalTo(1));
	}

	@Test
	public void testRemoveAddress() {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);
		addressSet.addAddress(nodeAddress);

		// when
		addressSet.removeAddress(nodeAddress);

		// then
		assertThat(addressSet.getAddressesOfNeighbours().size(), equalTo(0));
	}

	@Test
	public void testRemoveAllAddresses() {
		// given
		List<NodeAddress> listOfAddresses = Lists.newArrayList();
		for (int i = 0; i < 10; i++) {
			NodeAddress nodeAddress = mock(NodeAddress.class);
			listOfAddresses.add(nodeAddress);
			addressSet.addAddress(nodeAddress);
		}

		// when
		addressSet.removeAllAddresses();

		// then
		assertThat(addressSet.getAddressesOfNeighbours().size(), equalTo(0));
	}

	@Test
	public void testAddCacheModificationListener() {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);
		CacheModificationListener cacheModificationListener = mock(CacheModificationListener.class);

		// when
		addressSet.addCacheModificationListener(cacheModificationListener);
		addressSet.addAddress(nodeAddress);
		addressSet.removeAddress(nodeAddress);
		addressSet.removeAllAddresses();

		// then
		verify(cacheModificationListener, times(3)).onCacheChanged();
	}

	@Test(timeout = 4000)
	public void testTimeoutingEntries() throws InterruptedException {
		// given
		NodeAddress nodeAddress = mock(NodeAddress.class);
		CacheModificationListener cacheModificationListener = mock(CacheModificationListener.class);

		// when
		addressSet.addCacheModificationListener(cacheModificationListener);
		addressSet.addAddress(nodeAddress);

		// Make time flow faster
		ticker.increaseValue(TimeUnit.SECONDS.toNanos(2));

		// then
		verify(cacheModificationListener, atLeastOnce()).onCacheChanged();
		assertThat(addressSet.getAddressesOfNeighbours().size(), equalTo(0));
	}

}
