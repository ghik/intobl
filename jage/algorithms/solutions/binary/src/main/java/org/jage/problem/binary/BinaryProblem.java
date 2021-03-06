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
 * Created: 2011-11-03
 * $Id: BinaryProblem.java 472 2012-10-30 21:26:31Z krzywick $
 */

package org.jage.problem.binary;

import org.jage.problem.ParallelProblem;

/**
 * CountProblem - counts set bits in solution.
 *
 * @author AGH AgE Team
 */
public class BinaryProblem extends ParallelProblem<Boolean> {

	/**
	 * Creates a Count Problem.
	 *
	 * @param dimension
	 *            The dimension of this problem.
	 */
	public BinaryProblem(final Integer dimension) {
		super(dimension, Boolean.FALSE, Boolean.TRUE);
	}
}
