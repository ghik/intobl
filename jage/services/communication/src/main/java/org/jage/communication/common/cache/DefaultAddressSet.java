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
 * $Id: DefaultAddressSet.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.common.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.component.DefaultComponentAddress;
import org.jage.address.component.ComponentAddress;
import org.jage.address.node.NodeAddress;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;

/**
 * A simple, list based, database of node addresses.
 * <p>
 * 
 * This set also provides basic timeout-control capability. It uses Guava cache as a backing implementation.
 * 
 * @author AGH AgE Team
 */
public class DefaultAddressSet implements AddressSet, RemovalListener<NodeAddress, Boolean> {

	private static final Logger log = LoggerFactory.getLogger(DefaultAddressSet.class);

	@GuardedBy("itself")
	private final List<CacheModificationListener> listeners = Collections
	        .synchronizedList(new ArrayList<CacheModificationListener>());

	private static final String COMPONENT_ID = "communicationService";

	private static final Long DEFAULT_TTL = 6000L; // 6s

	// Guava has only map-backed cache, we just use always true Boolean as a value.
	private final Cache<NodeAddress, Boolean> cache;

	/**
	 * Default constructor - uses default configuration values.
	 */
	public DefaultAddressSet() {
		this(DEFAULT_TTL);
	}

	/**
	 * Constructs a new address set instance with given configuration values.
	 * 
	 * @param timeToLive
	 *            a time to live of cache entries (in s).
	 */
	public DefaultAddressSet(final long timeToLive) {
		this(timeToLive, Ticker.systemTicker());
	}

	/**
	 * Constructs a new address set instance with given configuration values.
	 * 
	 * @param timeToLive
	 *            a time to live of cache entries (in s).
	 * @param ticker
	 *            a ticker for the cache.
	 */
	DefaultAddressSet(final long timeToLive, final Ticker cacheTicker) {
		super();
		this.cache = CacheBuilder.newBuilder().expireAfterWrite(timeToLive, TimeUnit.SECONDS).ticker(cacheTicker)
		        .build();
	}

	@Override
	public void addAddress(final NodeAddress nodeAddress) {
		cache.put(nodeAddress, Boolean.TRUE);
		informOfCacheChange();
	}

	@Override
	public void removeAddress(final NodeAddress nodeAddress) {
		cache.invalidate(nodeAddress);
		informOfCacheChange();
	}

	@Override
	public void removeAllAddresses() {
		cache.invalidateAll();
		informOfCacheChange();
	}

	private void informOfCacheChange() {
		synchronized (listeners) {
			for (final CacheModificationListener listener : listeners) {
				listener.onCacheChanged();
			}
		}
	}

	@Override
	@Nonnull
	public Collection<ComponentAddress> getAddressesOfNeighbours() {
		final List<ComponentAddress> result = Lists.newLinkedList();

		for (final NodeAddress address : cache.asMap().keySet()) {
			result.add(new DefaultComponentAddress(COMPONENT_ID, address));
		}

		return result;
	}

	@Override
	@Nonnull
	public Collection<NodeAddress> getAddressesOfAllNodes() {
		return Lists.newLinkedList(cache.asMap().keySet());
	}

	@Override
	public void addCacheModificationListener(final CacheModificationListener listener) {
		listeners.add(listener);
	}

	@Override
	public long getNodesCount() {
		return cache.size();
	}

	@Override
	public void onRemoval(final RemovalNotification<NodeAddress, Boolean> notification) {
		informOfCacheChange();
	}

}
