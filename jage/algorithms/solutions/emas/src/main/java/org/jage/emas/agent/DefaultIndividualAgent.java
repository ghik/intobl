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
 * $Id: DefaultIndividualAgent.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.emas.agent;

import static java.lang.Math.max;

import javax.inject.Inject;

import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.ActionDrivenAgent;
import org.jage.solution.ISolution;

import static com.google.common.base.Objects.toStringHelper;

/**
 * Default implementation of {@link IndividualAgent}.
 *
 * @author AGH AgE Team
 */
public class DefaultIndividualAgent extends ActionDrivenAgent implements IndividualAgent {

	private static final long serialVersionUID = 1L;

	private double energy;

	private ISolution solution;

	private double originalFitness;

	private double effectiveFitness;

	@Inject
	private DefaultIndividualAgent(final AgentAddressSupplier supplier) {
	    super(supplier);
    }

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void changeEnergyBy(final double energyChange) {
		energy = max(energy + energyChange, 0.0);
	}

	@Override
	public ISolution getSolution() {
		return solution;
	}

	@Override
	public void setSolution(final ISolution solution) {
		this.solution = solution;
	}

	@Override
	public double getOriginalFitness() {
	    return originalFitness;
	}

    @Override
    public void setOriginalFitness(final double fitness) {
	    originalFitness = fitness;
	    setEffectiveFitness(fitness);
    }

    @Override
    public double getEffectiveFitness() {
	    return effectiveFitness;
    }

    @Override
    public void setEffectiveFitness(final double fitness) {
	    effectiveFitness = fitness;
    }

	@Override
	public IslandAgent getEnvironment() {
		return (IslandAgent)super.getAgentEnvironment();
	}

	@Override
	public String toString() {
		return toStringHelper(this)
				.add("address", getAddress())
				.add("energy", energy)
				.add("originalFitness", originalFitness)
				.add("effectiveFitness", effectiveFitness)
				.toString();
	}
}
