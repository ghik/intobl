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
 * Created: 2012-02-02
 * $Id: ActionDrivenAggregate.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.agent;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.action.Action;
import org.jage.action.preparators.IActionPreparator;
import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.platform.component.exception.ComponentException;
import org.jage.property.PropertyField;

/**
 * This aggregate implementation relies on a {@link IActionPreparator} to provide its actual behavior.
 * <p>
 * Given the aggregate state and environment, the {@link IActionPreparator} prepares an appropriate action, which is
 * then run by the aggregate.
 * <p>
 * Then, the aggregate runs the step methods of its children.
 *
 * @author AGH AgE Team
 */
public class ActionDrivenAggregate extends SimpleAggregate {

	/**
	 * ActionDrivenAggregate properties.
	 *
	 * @author AGH AgE Team
	 */
	public static class Properties {

		/**
		 * The actual step of computation.
		 */
		public static final String STEP = "step";
	}

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(ActionDrivenAggregate.class);

	@Inject
	private IActionPreparator<ActionDrivenAggregate> actionPreparator;

	@PropertyField(propertyName = Properties.STEP)
	private long step = 0;

	private ActionDrivenAggregateActionService actionService;

	public ActionDrivenAggregate(final AgentAddress address) {
	    super(address);
    }

	@Inject
	public ActionDrivenAggregate(final AgentAddressSupplier supplier) {
	    super(supplier);
    }

	public long getStep() {
		return step;
	}

	@Override
	public void init() throws ComponentException {
		super.init();
		if (!(getActionService() instanceof ActionDrivenAggregateActionService)) {
			throw new AggregateException(String.format("%s was expected as an action service.",
			        ActionDrivenAggregateActionService.class));
		}
		actionService = (ActionDrivenAggregateActionService)getActionService();
	}

	@Override
	public void step() {
		// invoke on children
		super.step();
		actionService.performPostponedActions();

		try {
			final List<Action> actions = actionPreparator.prepareActions(this);
			log.debug("Step {}, Submitting actions: {}", step, actions);
			doActions(actions);
		} catch (final AgentException e) {
			log.error("An exception occurred during the action call", e);
		}

		step++;
		notifyMonitorsForChangedProperties();
	}
}
