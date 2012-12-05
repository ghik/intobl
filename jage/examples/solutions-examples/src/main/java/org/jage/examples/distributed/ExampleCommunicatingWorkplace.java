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
package org.jage.examples.distributed;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.communication.message.IMessage;
import org.jage.workplace.ConnectedSimpleWorkplace;

import static org.jage.communication.message.Messages.newBroadcastMessage;

import static com.google.common.collect.Iterables.consumingIterable;

/**
 * This is an example workplace that communicates with other workspaces in its environment.
 *
 * @author AGH AgE Team
 */
public class ExampleCommunicatingWorkplace extends ConnectedSimpleWorkplace {

	private static final long serialVersionUID = 9146385144435844359L;

	private static final Logger log = LoggerFactory.getLogger(ExampleCommunicatingWorkplace.class);

	public ExampleCommunicatingWorkplace(final AgentAddress address) {
		super(address);
	}

	@Inject
	public ExampleCommunicatingWorkplace(final AgentAddressSupplier supplier) {
		super(supplier);
	}

	/**
	 * Performs step of all agents and processes events.
	 */
	@Override
	public void step() {
		for (final IMessage<AgentAddress, ?> message : consumingIterable(getMessages())) {
			log.info("{} received message from {}.", getAddress(), message.getHeader().getSenderAddress());
		}

		if (getStep() % 10 == 0) {
			final IMessage<AgentAddress, String> messageToSend = newBroadcastMessage(getAddress(), "Message");
			log.info("{} is sending a message {}.", getAddress(), messageToSend);
			getWorkplaceEnvironment().sendMessage(messageToSend);
		}

		super.step();
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
			log.warn("Interrupted", e);
		}
	}

}
