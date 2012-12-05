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

package org.jage.genetic.agent;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.platform.component.exception.ComponentException;
import org.jage.population.IPopulation;
import org.jage.solution.ISolution;

import com.google.common.base.Joiner;

public class StatsCollectingAgent extends GeneticActionDrivenAgent {
	private static final Joiner JOINER = Joiner.on(',');

	private String statsFilename;
	private int steps;

	private PrintWriter statsWriter;

	public StatsCollectingAgent(final AgentAddress address) {
		super(address);
	}

	@Inject
	public StatsCollectingAgent(final AgentAddressSupplier supplier) {
		super(supplier);
	}

	public void setStatsFilename(String statsFilename) {
		this.statsFilename = statsFilename;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}

	@Override
	public void init() throws ComponentException {
		try {
			super.init();
			statsWriter = new PrintWriter(statsFilename);
		} catch (IOException e) {
			throw new ComponentException(e);
		}
	}

	@Override
	public boolean finish() throws ComponentException {
		statsWriter.close();
		return super.finish();
	}

	@Override
	public void step() {
		super.step();
		long step = (Long) getProperty(Properties.STEP).getValue() - 2;

		@SuppressWarnings("unchecked")
		IPopulation.Tuple<ISolution, ?> solutionTuple = (IPopulation.Tuple<ISolution, ?>) getProperty(
				Properties.BEST_EVER).getValue();

		Object evaluation = null;
		if (solutionTuple != null) {
			evaluation = solutionTuple.getEvaluation();
		}

		if (evaluation != null && step < steps) {
			statsWriter.println(JOINER.join(step, evaluation));
		}
	}
}
