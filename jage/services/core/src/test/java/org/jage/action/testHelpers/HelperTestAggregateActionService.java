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
 * Created: 2008-10-07
 * $Id: HelperTestAggregateActionService.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.action.testHelpers;

import java.util.Collection;
import java.util.UUID;

import org.junit.Ignore;

import static org.mockito.Mockito.mock;

import org.jage.action.Action;
import org.jage.action.ActionPhase;
import org.jage.action.AgentAction;
import org.jage.action.SingleAction;
import org.jage.address.agent.DefaultAgentAddress;
import org.jage.address.agent.AgentAddress;
import org.jage.address.node.NodeAddress;
import org.jage.agent.AgentException;
import org.jage.agent.AggregateActionService;
import org.jage.agent.IAgent;
import org.jage.agent.ISimpleAgent;
import org.jage.agent.SimpleAggregate;
import org.jage.strategy.SupportedAgent;

/**
 * A sample implementation of aggregate that does nothing.
 * 
 * @author AGH AgE Team
 */
public class HelperTestAggregateActionService extends AggregateActionService {

	/**
	 * This is our version of {@link org.jage.agent.AbstractAggregate}'s processEvent method, that instead of logging
	 * the gotten exceptions, rethrows them (wrapping them in ActionTestException (a subclass of RuntimeException)
	 * because that's the only thing we can do without redefining the moment)
	 */
	@Override
	public void processActions() {
		Action action;
		while ((action = actionQueue.poll()) != null) {
			try {
				processAction(action);
			} catch (AgentException e) {
				throw new ActionTestException("Unknown event to process: " + e, e);
			}
		}
	}

	/**
	 * Exposes overridden method as public for testing purpose. {@inheritDoc}
	 */
	@Override
	public Collection<AgentAddress> initializeAction(Action action) throws AgentException {
		return super.initializeAction(action);
	}

	/**
	 * Exposes overridden method as public for testing purpose. {@inheritDoc}
	 */
	@Override
	public void initializeActionTargets(Action action, Collection<AgentAddress> all, Collection<AgentAddress> used) {
		super.initializeActionTargets(action, all, used);
	}

	/**
	 * Exposes overridden method as public for testing purpose. {@inheritDoc}
	 */
	@Override
	public void executeAction(Action action) throws AgentException {
		super.executeAction(action);
	}

	@AgentAction(name = "simpleTestAction")
	protected void performSimpleTestAction(IAgent target, SimpleTestActionContext context) {
		context.actionRun = true;
	}

	@SuppressWarnings("unused")
	@AgentAction(name = "privateTestAction")
	private void performPrivateTestAction(IAgent target, PrivateTestActionContext context) throws Exception {
		context.actionRun = true;
	}

	@AgentAction(name = "packageTestAction")
	void performPackageTestAction(IAgent target, PackageTestActionContext context) throws Exception {
		context.actionRun = true;
	}

	@AgentAction(name = "mult1Action")
	protected void performMult1Action(IAgent target, MultipleActionContext context) throws Exception {
		context.actionRun = true;
	}

	@AgentAction(name = "mult2Action")
	protected void performMult2Action(IAgent target, MultipleActionContext context) throws Exception {
		context.actionRun = true;
	}

	@AgentAction(name = "c1Action")
	protected void performComplex1Action(IAgent target, TracingActionContext context) throws Exception {
		context.trace += "1";
	}

	@AgentAction(name = "c2Action")
	protected void performComplex2Action(IAgent target, TracingActionContext context) throws Exception {
		context.trace += "2";
	}

	@AgentAction(name = "c3Action")
	protected void performComplex3Action(IAgent target, TracingActionContext context) throws Exception {
		context.trace += "3";
	}

	@AgentAction(name = "c4Action")
	protected void performComplex4Action(IAgent target, TracingActionContext context) throws Exception {
		context.trace += "4";
	}

	@AgentAction(name = "passToParentAction")
	protected void performPassToParentTestAction(ISimpleAgent target, PassToParentTestActionContext context) {
		context.actionTarget = target;
	}

	@AgentAction(name = "doNotPassToParentAction")
	protected void performDoNotPassToParentTestAction(ISimpleAgent target, DoNotPassToParentTestActionContext context) {
		context.actionTarget = target;
	}

	@AgentAction(name = "mixedAggregate", phase = ActionPhase.INIT)
	protected void performMixedAggregateAction(SingleAction action) {
		((MixedActionContext)action.getContext()).setExecAggr(true);
	}
}
