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
 * Created: 2008-10-07
 * $Id: IsolatedSimpleWorkplace.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.workplace;

import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.IAgent;
import org.jage.query.AgentEnvironmentQuery;

/**
 * This is a complete workplace which doesn't communicate with any other workplaces (works as single/isolated
 * workplace).
 *
 * @author AGH AgE Team
 */
public class IsolatedSimpleWorkplace extends SimpleWorkplace {

	private static final long serialVersionUID = 257256225676180951L;

	private static final Logger log = LoggerFactory.getLogger(IsolatedSimpleWorkplace.class);

	public IsolatedSimpleWorkplace(final AgentAddress address) {
		super(address);
	}

	@Inject
	public IsolatedSimpleWorkplace(final AgentAddressSupplier supplier) {
		super(supplier);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The isolated workplace cannot query a parent. This method always throws the {@link IllegalOperationException}.
	 */
	@Override
	public <E extends IAgent, T> Collection<T> queryParent(final AgentEnvironmentQuery<E, T> query) {
		throw new IllegalOperationException("Isolated workplace cannot query a parent.");
	}

	@Override
	public void run() {
		setRunning();
		log.debug("{} has been started", getAddress());
		while (isRunning()) {
			step();
		}
		setStopped();
	}

}
