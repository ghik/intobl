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
 * Created: 2011-10-20
 * $Id: NonParallelProblem.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.problem;

import org.jage.property.ClassPropertyContainer;

/**
 * A non parallel problem that is bounded by the following domain:
 *
 * <pre>
 * [min[0], max[0]] x [min[1], max[1]] x ... x [min[dimension-1], max[dimension-1]]
 * </pre>
 *
 * @param <R>
 *            The type of the problem bounds
 *
 * @author AGH AgE Team
 */
public class NonParallelProblem<R> extends ClassPropertyContainer implements IVectorProblem<R> {

	private int dimension;

	private R[] min;

	private R[] max;

	/**
	 * Creates a NonParallelProblem with the given upper and lower bounds.
	 *
	 * @param min
	 *            the lower bounds
	 * @param max
	 *            the upper bounds
	 */
	public NonParallelProblem(R[] min, R[] max) {
		if (min.length != max.length) {
			throw new IllegalArgumentException("Min and max arrays must have same lenght");
		}

		this.min = min;
		this.max = max;
		this.dimension = min.length;
	}

	@Override
	public final int getDimension() {
		return dimension;
	}

	@Override
	public final R lowerBound(int atDimension) {
		checkDimension(atDimension);
		return min[atDimension];
	}

	@Override
	public final R upperBound(int atDimension) {
		checkDimension(atDimension);
		return max[atDimension];
	}

	private void checkDimension(int atDimension) {
		if (atDimension < 0 || atDimension >= this.dimension) {
			throw new IllegalArgumentException("Dimension out of range");
		}
	}
}
