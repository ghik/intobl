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
 * Created: 2012-03-18
 * $Id: Battle.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.emas.battle;

import org.jage.agent.IAgent;
import org.jage.strategy.IStrategy;

/**
 * Strategy for battles between agents.
 *
 * @param <A>
 *            the type of agents
 *
 * @author AGH AgE Team
 */
public interface Battle<A extends IAgent> extends IStrategy {

	/**
	 * Carry out a fight between two agents.
	 *
	 * @param first
	 *            the first agent
	 * @param second
	 *            the second agent
	 * @return the winner
	 */
	A fight(A first, A second);
}
