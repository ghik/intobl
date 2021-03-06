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
 * Created: 2011-12-21
 * $Id: AbstractPreselectionTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.genetic.preselection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jage.population.IPopulation;
import org.jage.solution.IVectorSolution;

import static org.jage.population.Populations.emptyPopulation;
import static org.jage.population.Populations.singletonPopulation;

/**
 * Tests for AbstractPreselection.
 *
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractPreselectionTest {

	@InjectMocks
	private AbstractPreselection tested = new AbstractPreselection() {

		@Override
		protected int[] getPreselectedIndices(double[] values) {
			int[] indices = new int[values.length];
			for (int i = 0; i < indices.length; i++) {
				indices[i] = i;
			}
			return indices;
		}
	};

	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionForNullPopulation() {
		// given
		IPopulation<IVectorSolution<Double>, Double> population = null;

		// when
		tested.preselect(population);
	}

	@Test
	public void shouldReturnEmptyForEmptyPopulation() {
		// given
		IPopulation<IVectorSolution<Double>, Double> population = emptyPopulation();

		// when
		IPopulation<IVectorSolution<Double>, Double> preselectedPopulation = tested.preselect(population);

		// then
		assertTrue(preselectedPopulation.isEmpty());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldReturnSameWhenOneSolution() {
		// given
		IVectorSolution<Double> solution = Mockito.mock(IVectorSolution.class);
		IPopulation<IVectorSolution<Double>, Double> population = singletonPopulation(solution);

		// when
		IPopulation<IVectorSolution<Double>, Double> preselectedPopulation = tested.preselect(population);

		// then
		assertEquals(1, preselectedPopulation.size());
		assertTrue(preselectedPopulation.contains(solution));
	}

	// TODO test respect of indices
	// TODO test factory invocation

}
