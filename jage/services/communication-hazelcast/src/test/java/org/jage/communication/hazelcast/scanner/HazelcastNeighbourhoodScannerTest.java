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
 * $Id: HazelcastNeighbourhoodScannerTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast.scanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.jage.address.node.NodeAddressSupplier;
import org.jage.address.node.NodeAddress;
import org.jage.communication.common.cache.AddressSet;
import org.jage.communication.hazelcast.HazelcastNodeAddress;

import com.google.common.collect.ImmutableSet;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;

/**
 * Tests for the {@link HazelcastNeighbourhoodScanner} class.
 * 
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class HazelcastNeighbourhoodScannerTest {

	@Mock
	private AddressSet addressSet;

	@Mock
	private NodeAddressSupplier addressProvider;

	@Mock
	private HazelcastInstance hazelcastInstance;

	@Mock
	private LifecycleService lifecycleService;

	@Mock
	private Cluster cluster;

	@Mock
	private Member memberMe;

	@Mock
	private Member memberOther;

	final NodeAddress addressMe = new HazelcastNodeAddress("1");

	final NodeAddress addressOther = new HazelcastNodeAddress("2");

	@InjectMocks
	private HazelcastNeighbourhoodScanner scanner = new HazelcastNeighbourhoodScanner(1, null);

	@Before
	public void setUp() {
		given(hazelcastInstance.getCluster()).willReturn(cluster);
		given(hazelcastInstance.getLifecycleService()).willReturn(lifecycleService);
		given(cluster.getMembers()).willReturn(ImmutableSet.of(memberMe));
		given(memberMe.getUuid()).willReturn("1");
		given(memberMe.localMember()).willReturn(true);
		given(memberOther.getUuid()).willReturn("2");
		given(addressProvider.get()).willReturn(addressMe);
	}

	@Test
	public void testNewNeighbourWasDetected() {
		// when
		scanner.init();
		scanner.memberAdded(new MembershipEvent(cluster, memberOther, MembershipEvent.MEMBER_ADDED));

		// then
		scanner.finish();

		verify(cluster).addMembershipListener(scanner);
		verify(addressSet, atLeastOnce()).addAddress(addressMe);
		verify(addressSet).addAddress(addressOther);
	}

	@Test
	public void testNeighbourDisappeared() {
		// given
		scanner.init();
		scanner.memberAdded(new MembershipEvent(cluster, memberOther, MembershipEvent.MEMBER_ADDED));

		// when
		scanner.memberRemoved(new MembershipEvent(cluster, memberOther, MembershipEvent.MEMBER_REMOVED));

		// then
		scanner.finish();

		verify(addressSet).removeAddress(addressOther);
		verify(addressSet, never()).removeAddress(addressMe);
	}
}
