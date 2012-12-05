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
 * $Id: HazelcastNeighbourhoodScanner.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast.scanner;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.node.NodeAddress;
import org.jage.communication.common.cache.AddressSet;
import org.jage.communication.common.scanner.NeighbourhoodScanner;
import org.jage.communication.hazelcast.HazelcastInstanceFactory;
import org.jage.communication.hazelcast.HazelcastNodeAddress;
import org.jage.communication.hazelcast.HazelcastNodeAddressProvider;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

/**
 * A Hazelcast based neighbourhood scanner for CommunicationManager.
 * <p>
 * The scanner uses the cluster membership information and events to gather data about neighbours.
 * <p>
 * With this implementation one must use {@link HazelcastNodeAddressProvider} and {@link HazelcastNodeAddress}!
 *
 * @see HazelcastNodeAddressProvider
 * @see HazelcastNodeAddress
 * @since 2.6
 *
 * @author AGH AgE Team
 */
public class HazelcastNeighbourhoodScanner implements MembershipListener, NeighbourhoodScanner, Runnable {

	private static final Logger log = LoggerFactory.getLogger(HazelcastNeighbourhoodScanner.class);

	private static final int DEFAULT_REFRESH_DELAY = 3000;

	private final int refreshDelay;

	// Because of tests (@InjectMocks) it cannot be final!
	private HazelcastInstance hazelcastInstance;

	@Inject
	private AddressSet cache;

	private volatile State state = State.NOT_REALIZED;

	private Thread refreshingThread;

	/**
	 * Default constructor - creates the scanner with default configuration values.
	 */
	public HazelcastNeighbourhoodScanner() {
		this(DEFAULT_REFRESH_DELAY);
	}

	/**
	 * Constructs a new scanner instance.
	 *
	 * @param refreshDelay
	 *            specifies how long (in ms) should scanner wait before sending a next message.
	 */
	public HazelcastNeighbourhoodScanner(final int refreshDelay) {
		super();
		this.refreshDelay = refreshDelay;
		hazelcastInstance = HazelcastInstanceFactory.getInstance();
	}

	/**
	 * Package-visible constructor for mocking.
	 *
	 * @param refreshDelay
	 *            specifies how long (in ms) should scanner wait before sending a next message.
	 * @param hazelcastInstance
	 *            a Hazelcast instance to use.
	 */
	HazelcastNeighbourhoodScanner(final int refreshDelay, @Nullable final HazelcastInstance hazelcastInstance) {
		super();
		this.refreshDelay = refreshDelay;
		this.hazelcastInstance = hazelcastInstance;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Attaches the membership listener to the cluster and scans its members for the first time to initialise the
	 * address cache.
	 */
	@Override
	public void init() {
		log.info("Initializing the HazelcastNeighbourhoodScanner.");
		state = State.INITIALIZING;

		final Cluster cluster = hazelcastInstance.getCluster();
		cluster.addMembershipListener(this);

		for (final Member member : cluster.getMembers()) {
			addMemberToCache(member);
		}

		// start the control protocol implementing thread
		refreshingThread = new Thread(this);
		refreshingThread.start();
		log.info("Done with initialization of the HazelcastNeighbourhoodScanner.");
	}

	@Override
	public boolean finish() {
		log.info("Finalizing the HazelcastNeighbourhoodScanner.");

		if (hazelcastInstance.getLifecycleService().isRunning()) {
			final Cluster cluster = hazelcastInstance.getCluster();
			cluster.removeMembershipListener(this);
		}

		state = State.FINISHED;
		try {
			refreshingThread.join(refreshDelay);
		} catch (final InterruptedException e) {
			log.error("Interrupted while waiting for thread.", e);
		}

		if (hazelcastInstance.getLifecycleService().isRunning()) {
			hazelcastInstance.getLifecycleService().shutdown();
		}

		log.info("Done with finalization of the HazelcastNeighbourhoodScanner.");

		return true;
	}

	/**
	 * A loop implementing the refreshing thread.
	 */
	@Override
	public void run() {
		log.debug("Membership refresher running.");

		final Cluster cluster = hazelcastInstance.getCluster();

		state = State.RUNNING;
		while (state == State.RUNNING) {
			for (final Member member : cluster.getMembers()) {
				addMemberToCache(member);
			}
			try {
				Thread.sleep(DEFAULT_REFRESH_DELAY);
			} catch (final InterruptedException e) {
				log.error("Interrupted.");
			}
		}
	}

	private enum State {
		NOT_REALIZED, INITIALIZING, RUNNING, FINISHED;
	}

	@Override
	public boolean isAlive() {
		return state == State.RUNNING;
	}

	@Override
	public boolean isStarting() {
		return state == State.NOT_REALIZED || state == State.INITIALIZING;
	}

	@Override
	public boolean isStopped() {
		return state == State.FINISHED;
	}

	@Override
	public void memberAdded(final MembershipEvent membershipEvent) {
		log.debug("Membership event: {}.", membershipEvent);
		addMemberToCache(membershipEvent.getMember());
	}

	@Override
	public void memberRemoved(final MembershipEvent membershipEvent) {
		log.debug("Membership event: {}.", membershipEvent);
		evictMemberFromCache(membershipEvent.getMember());
	}

	private void addMemberToCache(final Member member) {
		final String uuid = member.getUuid();
		final NodeAddress nodeAddress = new HazelcastNodeAddress(uuid);
		log.debug("Node {} is in cluster.", nodeAddress);
		cache.addAddress(nodeAddress);
	}

	private void evictMemberFromCache(final Member member) {
		final String uuid = member.getUuid();
		final NodeAddress nodeAddress = new HazelcastNodeAddress(uuid);
		log.debug("Node {} is not in cluster anymore.", nodeAddress);
		cache.removeAddress(nodeAddress);
	}
}
