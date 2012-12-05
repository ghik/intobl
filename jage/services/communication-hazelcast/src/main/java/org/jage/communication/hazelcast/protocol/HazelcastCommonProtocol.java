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
 * Created: 2012-03-15
 * $Id: HazelcastCommonProtocol.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast.protocol;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.node.NodeAddressSupplier;
import org.jage.address.node.NodeAddress;
import org.jage.communication.CommunicationException;
import org.jage.communication.common.protocol.CommunicationProtocol;
import org.jage.communication.common.protocol.MessageReceivedListener;
import org.jage.communication.hazelcast.HazelcastInstanceFactory;

import com.google.common.collect.Lists;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.InstanceDestroyedException;
import com.hazelcast.core.MemberLeftException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Common parts of Hazelcast-based communication protocols.
 *
 * @author AGH AgE Team
 */
public abstract class HazelcastCommonProtocol implements CommunicationProtocol {

	private static final Logger log = LoggerFactory.getLogger(HazelcastCommonProtocol.class);

	@Inject
	private NodeAddressSupplier addressProvider;

	private NodeAddress localAddress;

	// Because of tests (@InjectMocks) it cannot be final!
	private HazelcastInstance hazelcastInstance;

	private final List<MessageReceivedListener> listeners = Lists.newCopyOnWriteArrayList();

	/**
	 * Creates a new Hazelcast-based protocol.
	 */
	public HazelcastCommonProtocol() {
		hazelcastInstance = HazelcastInstanceFactory.getInstance();
	}

	/**
	 * Package-visible constructor for mocking.
	 *
	 * @param hazelcastInstance
	 *            a Hazelcast instance to use.
	 */
	HazelcastCommonProtocol(@Nullable final HazelcastInstance hazelcastInstance) {
		this.hazelcastInstance = hazelcastInstance;
	}

	/**
	 * Initializes a Hazelcast instance. It also gets the local AgE address from the provider.
	 * <p>
	 *
	 * {@inheritDoc}
	 *
	 * @see org.jage.communication.common.protocol.CommunicationProtocol#init()
	 */
	@Override
	public void init() {
		log.info("Initializing the Hazelcast-based protocol.");
		localAddress = addressProvider.get();
	}

	/**
	 * Shutdowns the Hazelcast instance.
	 * <p>
	 *
	 * {@inheritDoc}
	 *
	 * @see org.jage.communication.common.protocol.CommunicationProtocol#finish()
	 */
	@Override
	public boolean finish() {
		log.info("Finalizing the Hazelcast-based protocol.");

		if (isAlive()) {
			getHazelcastInstance().getLifecycleService().shutdown();
		}

		log.info("Done with finalization of the Hazelcast-based protocol.");

		return true;
	}

	@Override
	public void addMessageReceivedListener(final MessageReceivedListener listener) {
		getListeners().add(checkNotNull(listener));
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 *
	 * Note: this implementation may cause a deadlock, if not all nodes reach the barrier during their lifetime.
	 * <p>
	 *
	 * This implementation is based on the count provided by the external manager. If a participating node shutdowns
	 * before reaching the barrier all remaining nodes will be deadlocked.
	 *
	 * @throws InterruptedException
	 *             when the waiting thread was interrupted.
	 */
	@Override
	public void barrier(final String key, final long count) throws InterruptedException {
		checkNotNull(key);
		checkArgument(count > 0, "Count must be greater than 0 for a barrier to work.");

		log.debug("Trying to enter the barrier {} with the count {}.", key, count);

		final ICountDownLatch countDownLatch = getHazelcastInstance().getCountDownLatch(key);
		if (countDownLatch.setCount((int)count)) {
			log.debug("I have created the barrier.");
		}
		countDownLatch.countDown();
		try {
			countDownLatch.await();
			log.debug("Leaving the barrier {} succesfully.", key);

		} catch (final MemberLeftException e) {
			log.error("Exception during the latch await.", e);
			throw new CommunicationException(e);
		} catch (final InstanceDestroyedException e) {
			log.error("Exception during the latch await.", e);
			throw new CommunicationException(e);
		} catch (final InterruptedException e) {
			log.info("Interrupted during the latch await. Destroying the barrier.", e);
			countDownLatch.destroy();
			Thread.currentThread().interrupt();
			throw e;
		} catch (final IllegalStateException e) {
			log.error("Exception during the latch await.", e);
			throw new CommunicationException(e);
		}
		// XXX: the deadlocking is at least partially solvable, but we need a consistent behaviour defined for
		// all environment changes.
		// Moreover, we need to think who should care about this - a communication protocol or the polling manager?
	}

	@Override
	public boolean isAlive() {
		return getHazelcastInstance().getLifecycleService().isRunning();
	}

	@Override
	public boolean isStopped() {
		return !isStarting() && !isAlive();
	}

	/**
	 * Returns the listeners that can receive messages.
	 *
	 * @return the listeners.
	 */
	protected final List<MessageReceivedListener> getListeners() {
		return listeners;
	}

	/**
	 * Returns the Hazelcast instance associated with this protocol.
	 *
	 * @return the Hazelcast instance associated with this protocol.
	 */
	public final HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}

	/**
	 * Returns the address of the local node.
	 *
	 * @return the address of the local node.
	 */
	public final NodeAddress getLocalAddress() {
		return localAddress;
	}
}
