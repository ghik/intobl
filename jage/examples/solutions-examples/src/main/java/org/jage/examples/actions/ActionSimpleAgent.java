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
 * Created: 2011-03-15
 * $Id: ActionSimpleAgent.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.examples.actions;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.action.SingleAction;
import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.address.selector.UnicastSelector;
import org.jage.agent.AgentException;
import org.jage.agent.SimpleAgent;
import org.jage.platform.component.exception.ComponentException;

/**
 * An agent showing how to order an action to perform by aggregate.
 *
 * @author AGH AgE Team
 */
public class ActionSimpleAgent extends SimpleAgent {

	private static final long serialVersionUID = 1L;

	private final Logger log = LoggerFactory.getLogger(ActionSimpleAgent.class);

	public ActionSimpleAgent(final AgentAddress address) {
	    super(address);
    }

	@Inject
	public ActionSimpleAgent(final AgentAddressSupplier supplier) {
	    super(supplier);
    }

	@Override
	public void step() {
		log.info("{} says Hello World from {}", getAddress(), getParentAddress());
		try {
			doAction(new SingleAction(new UnicastSelector<AgentAddress>(getAddress()), new SampleActionContext()));
		} catch (final AgentException e) {
			log.error("Agent exception", e);
			return;
		}

		try {
			Thread.sleep(200);
		} catch (final InterruptedException e) {
			log.error("Interrupted", e);
		}

	}

	@Override
	public boolean finish() {
		log.info("Finishing Action Simple Agent: {}", getAddress());
		return true;
	}

	@Override
	public void init() throws ComponentException {
		super.init();
		log.info("Initializing Action Simple Agent: {}", getAddress());
	}

}
