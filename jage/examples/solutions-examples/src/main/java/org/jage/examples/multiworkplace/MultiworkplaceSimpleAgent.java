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
 * Created: 2012-04-20
 * $Id: MultiworkplaceSimpleAgent.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.examples.multiworkplace;

import java.util.Collection;
import java.util.Random;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.action.AgentActions;
import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.address.selector.agent.ParentAgentAddressSelector;
import org.jage.agent.AgentException;
import org.jage.agent.IAgent;
import org.jage.agent.SimpleAgent;
import org.jage.communication.message.Header;
import org.jage.communication.message.IHeader;
import org.jage.communication.message.Message;
import org.jage.platform.component.exception.ComponentException;
import org.jage.property.PropertyField;
import org.jage.query.AgentEnvironmentQuery;

/**
 * This agent should be located just beneath a workplace.
 *
 * @author AGH AgE Team
 */
public class MultiworkplaceSimpleAgent extends SimpleAgent {

	private static Logger log = LoggerFactory.getLogger(MultiworkplaceSimpleAgent.class);

	private static final long serialVersionUID = 2L;

	private static final Random random = new Random();

	public MultiworkplaceSimpleAgent(final AgentAddress address) {
		super(address);
	}

	@Inject
	public MultiworkplaceSimpleAgent(final AgentAddressSupplier supplier) {
		super(supplier);
	}

	/**
	 * Steps counter
	 */
	@PropertyField(propertyName = "step")
	private transient int counter = 0;

	@Override
	public void step() {
		try {
			final IHeader<AgentAddress> header = new Header<AgentAddress>(getAddress(), new ParentAgentAddressSelector(
			        getAddress()));
			final Message<AgentAddress, String> message = new Message<AgentAddress, String>(header, "Message to parent");
			doAction(AgentActions.sendMessage(message));
		} catch (final AgentException e1) {
			log.error("Could not send a message.", e1);
		}

		counter++;
		if ((counter + hashCode()) % 50 == 0) {
			considerMigration();
		}

		try {
			Thread.sleep(10);
		} catch (final InterruptedException e) {
			log.error("Interrupted", e);
		}
	}

	/**
	 * Considers migration and migrates eventually
	 */
	private void considerMigration() {
		Collection<IAgent> answer;
		AgentAddress target = null;
		try {
			log.info("Queyring parent...");
			final AgentEnvironmentQuery<IAgent, IAgent> query = new AgentEnvironmentQuery<IAgent, IAgent>();

			answer = queryParentEnvironment(query);
			if (answer.size() > 1) {
				log.info("Agent: {} can migrate from {} to following environments:", getAddress(), getParentAddress());
				float max = 0;
				for (final IAgent entry : answer) {
					final AgentAddress possibleTargetAddress = entry.getAddress();
					if (possibleTargetAddress != getParentAddress()) {
						log.info("   " + possibleTargetAddress);
					}
					final float rand = random.nextFloat();
					if (max < rand) {
						max = rand;
						target = possibleTargetAddress;
					}
				}
			} else {
				log.info("Agent: {} can not migrate anywhere from: {}", getAddress(), getParentAddress());
			}

			if (target != null) {
				if (target != getParentAddress()) {
					log.info("Agent: {} decides to migrate to environment: {}", getAddress(), target);
					try {
						doAction(AgentActions.migrate(this, target));
					} catch (final AgentException e) {
						log.error("Can't move to: " + target + ".", e);
					}
				} else {
					log.info("Agent: {}  decides to stay in environment: {}", getAddress(), target);
				}
			}
		} catch (final AgentException e) {
			log.error("Agent exception", e);
		}
	}

	@Override
	public boolean finish() throws ComponentException {
		log.info("Finishing {}", getAddress());
		return super.finish();
	}

}
