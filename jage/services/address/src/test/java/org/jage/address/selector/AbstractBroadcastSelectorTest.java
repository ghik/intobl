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
 * $Id: AbstractBroadcastSelectorTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.selector;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.mock;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.DefaultAgentAddress;
import org.jage.address.node.NodeAddress;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * An abstract address selector test for broadcast selectors.
 * <p>
 * The only difference from {@link AbstractAddressSelectorTest} is handling of the <code>selected()</code> method for an
 * uninitialised selector.
 *
 * @author AGH AgE Team
 */
@Ignore
public abstract class AbstractBroadcastSelectorTest extends AbstractAddressSelectorTest {

	private final NodeAddress nodeAddress = mock(NodeAddress.class);

	@Test
	@Override
	public void testSelectedDifferent() {
		final AgentAddress addr = new DefaultAgentAddress(nodeAddress);
		assertTrue(getSelectorUnderTest().selected(addr));
		getSelectorUnderTest().initialize(all, used);
		assertFalse(getSelectorUnderTest().selected(addr));
	}

	@Override
	@Ignore
	protected void testSelected(final Collection<AgentAddress> all, final Collection<AgentAddress> used,
	        final Collection<AgentAddress> expected) {

		for (final AgentAddress addr : all) {
			assertTrue(getSelectorUnderTest().selected(addr));
		}

		getSelectorUnderTest().initialize(all, used);

		for (final AgentAddress addr : all) {
			if (expected.contains(addr)) {
				assertTrue(getSelectorUnderTest().selected(addr));
			} else {
				assertFalse(getSelectorUnderTest().selected(addr));
			}
		}
	}
}
