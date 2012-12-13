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
 * Created: 2012-01-30
 * $Id: StatisticsUpdateAction.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.emas.action.island;

import javax.inject.Inject;

import org.jage.agent.AgentException;
import org.jage.emas.agent.DefaultIslandAgent;
import org.jage.emas.util.BestFitnessTracer;
import org.jage.emas.util.ChainingAction;

/**
 * This action handler performs statistics update actions on island agents. It
 * triggers an island to recompute its statistics.
 * 
 * @author AGH AgE Team
 */
public final class FitnessTraceAction extends ChainingAction<DefaultIslandAgent> {

	@Inject
	private BestFitnessTracer bestFitnessTracer;

	public void setBestFitnessTracer(BestFitnessTracer bestFitnessTracer) {
		this.bestFitnessTracer = bestFitnessTracer;
	}

	@Override
	public void doPerform(final DefaultIslandAgent agent) throws AgentException {
		bestFitnessTracer.updateBestFitness(agent.getBestFitnessEver());
	}
}
