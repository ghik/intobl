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
 * Created: 2011-09-02
 * $Id: ConnectedSimpleWorkplace.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.workplace;

import java.util.Collection;

import javax.inject.Inject;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.IAgent;
import org.jage.communication.message.IMessage;
import org.jage.query.AgentEnvironmentQuery;

import static com.google.common.base.Preconditions.checkState;

/**
 * Workplace that can be used in a multiworkplace environment.
 * <p>
 * The only mechanism that it provides and that is not provided by a {@link SimpleWorkplace} is the query forwarding.
 *
 * @author AGH AgE Team
 */
public class ConnectedSimpleWorkplace extends SimpleWorkplace {

	private static final long serialVersionUID = -5703950513827082141L;

	public ConnectedSimpleWorkplace(final AgentAddress address) {
		super(address);
	}

	@Inject
	public ConnectedSimpleWorkplace(final AgentAddressSupplier supplier) {
		super(supplier);
	}

	@Override
	public <E extends IAgent, T> Collection<T> queryParent(final AgentEnvironmentQuery<E, T> query) {
		throw new UnsupportedOperationException("Workplaces have no parents.");
	}

	@Override
	protected <E extends IAgent, T> Collection<T> queryEnvironment(final AgentEnvironmentQuery<E, T> query) {
		checkState(hasWorkplaceEnvironment(), "Workplace has no environment.");
		return getWorkplaceEnvironment().queryWorkplaces(query);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * In the case of this workplace a message will be sent to other workplaces.
	 *
	 * @see org.jage.agent.SimpleAggregate#sendMessage(org.jage.communication.message.IMessage)
	 */
	@Override
	protected void sendMessage(final IMessage<AgentAddress, ?> message) {
		checkState(hasWorkplaceEnvironment(), "Workplace has no environment.");
		getWorkplaceEnvironment().sendMessage(message);
	}
}
