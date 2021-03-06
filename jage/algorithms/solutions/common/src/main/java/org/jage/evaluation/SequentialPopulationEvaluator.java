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
 * Created: 2011-12-01
 * $Id: SequentialPopulationEvaluator.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.evaluation;

import javax.inject.Inject;

import org.jage.population.IPopulation;
import org.jage.property.ClassPropertyContainer;
import org.jage.solution.ISolution;

/**
 * Simple {@link IPopulationEvaluator} strategy, which evaluates solutions sequentially, one by one.
 *
 * @param <S>
 *            The type of {@link ISolution} to be evaluated
 * @param <E>
 *            the type of evaluation
 * @author AGH AgE Team
 */
public class SequentialPopulationEvaluator<S extends ISolution, E> extends ClassPropertyContainer implements
        IPopulationEvaluator<S, E> {

	@Inject
	private ISolutionEvaluator<S, E> evaluator;

	@Override
	public void evaluatePopulation(final IPopulation<S, E> population) {
		for (final S solution : population) {
			population.setEvaluation(solution, evaluator.evaluate(solution));
		}
	}
}
