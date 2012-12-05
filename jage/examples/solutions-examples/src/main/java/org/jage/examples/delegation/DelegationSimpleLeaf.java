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
 * Created: 2011-04-07
 * $Id: DelegationSimpleLeaf.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.examples.delegation;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.SimpleAgent;

/**
 * This agent presents an example delegation of strategies. It is a leaf of tree of agents. It can accept an echo
 * strategy coming from a parent.
 *
 * @author AGH AgE Team
 */
public class DelegationSimpleLeaf extends SimpleAgent implements IEchoStrategyAcceptingAgent {

	private static final long serialVersionUID = 2L;

	private static final Logger log = LoggerFactory.getLogger(DelegationSimpleLeaf.class);

	private IEchoStrategy echoStrategy;


	public DelegationSimpleLeaf(final AgentAddress address) {
	    super(address);
    }

	@Inject
	public DelegationSimpleLeaf(final AgentAddressSupplier supplier) {
	    super(supplier);
    }

	/**
	 * Executes a step of the agent. This agent delegates its execution to the <em>echo strategy</em> and then sleeps
	 * for a while. {@inheritDoc}
	 *
	 * @see org.jage.agent.SimpleAgent#step()
	 */
	@Override
	public void step() {
		log.info("{} says Hello World from {}", getAddress(), getParentAddress());

		echoStrategy.echo(getParentAddress().toString());

		try {
			Thread.sleep(200);
		} catch (final InterruptedException e) {
			log.error("Interrupted", e);
		}
	}

	@Override
	public boolean finish() {
		log.info("Finishing Delegation Simple Leaf: {}", getAddress());
		return true;
	}

	@Override
	public void acceptEchoStrategy(final String echoStrategyName) {
		log.info("{} asked to accept a strategy {}", getAddress(), echoStrategyName);

		echoStrategy = (IEchoStrategy)instanceProvider.getInstance(echoStrategyName);
	}
}
