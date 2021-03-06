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
 * $Id: ProbabilisticFitnessBattle.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.emas.battle;

import javax.inject.Inject;

import org.jage.emas.agent.IndividualAgent;
import org.jage.random.INormalizedDoubleRandomGenerator;

/**
 * Probabilistic battle strategy based on fitness. Each agent can win, with a probability proportional to their relative
 * fitness.
 *
 * @author AGH AgE Team
 */
public class ProbabilisticFitnessBattle implements Battle<IndividualAgent> {

	@Inject
	private INormalizedDoubleRandomGenerator rand;

	@Override
	public IndividualAgent fight(final IndividualAgent first, final IndividualAgent second) {
		final double firstValue = first.getEffectiveFitness();
		final double secondValue = second.getEffectiveFitness();
		// FIXME
		final double threshold = 1 - firstValue / (firstValue + secondValue);
		return rand.nextDouble() <= threshold ? first : second;
	}
}
