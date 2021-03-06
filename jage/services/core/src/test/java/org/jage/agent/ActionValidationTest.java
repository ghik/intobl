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
 * $Id: ActionValidationTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.agent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.jage.action.Action;
import org.jage.action.NullInstanceProvider;
import org.jage.action.SingleAction;
import org.jage.action.context.PassToParentActionContext;
import org.jage.action.testHelpers.ActionTestException;
import org.jage.action.testHelpers.DoNotPassToParentTestActionContext;
import org.jage.action.testHelpers.HelperTestAggregateActionService;
import org.jage.action.testHelpers.PassToParentTestActionContext;
import org.jage.address.agent.AgentAddress;
import org.jage.address.selector.UnicastSelector;
import org.jage.address.selector.agent.ParentAgentAddressSelector;
import org.jage.platform.component.provider.IComponentInstanceProvider;

import static org.jage.address.selector.Selectors.parentOf;
import static org.jage.address.selector.Selectors.unicastFor;
import static org.jage.utils.AgentTestUtils.createMockAgentWithAddress;

/**
 * Tests for the {@link AggregateActionService} class: the action validation.
 *
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class ActionValidationTest {

	private final SimpleAggregate aggregate = new SimpleAggregate(mock(AgentAddress.class));

	private final HelperTestAggregateActionService actionService = new HelperTestAggregateActionService();

	private final SimpleAggregate aggregate2 = new SimpleAggregate(mock(AgentAddress.class));

	private final HelperTestAggregateActionService actionService2 = new HelperTestAggregateActionService();

	private final ISimpleAgent agent = createMockAgentWithAddress();

	private final ISimpleAgent agent2 = createMockAgentWithAddress();

	@Mock
	private IComponentInstanceProvider instanceProvider;

	@Before
	public void setUp() {
		actionService.setAggregate(aggregate);
		actionService.setInstanceProvider(instanceProvider);

		actionService2.setAggregate(aggregate2);
		actionService2.setInstanceProvider(new NullInstanceProvider());

		aggregate.setInstanceProvider(instanceProvider);
		aggregate.setActionService(actionService);
		aggregate2.setInstanceProvider(new NullInstanceProvider());
		aggregate2.setActionService(actionService2);

		aggregate.add(agent);
		aggregate.add(aggregate2);
		aggregate2.add(agent2);
	}

	// FIXME

	@Test(expected = ActionTestException.class)
	public void testNoSuchAgent() {
		// given
		final DoNotPassToParentTestActionContext context = new DoNotPassToParentTestActionContext();
		final UnicastSelector<AgentAddress> random = unicastFor(mock(AgentAddress.class));

		// when
		actionService.doAction(new SingleAction(random, context));
		actionService.processActions();
	}

	@Test
	public void testInvokeOnSelf() {
		// given
		final DoNotPassToParentTestActionContext context = new DoNotPassToParentTestActionContext();

		// when
		actionService2.doAction(new SingleAction(unicastFor(agent2.getAddress()), context));
		actionService2.processActions();

		// then
		assertThat(context.actionTarget, is(agent2));
	}

	@Test
	public void testInvokeOnOwnAggregate() {
		// given
		final DoNotPassToParentTestActionContext context = new DoNotPassToParentTestActionContext();

		// when
		actionService2.doAction(new SingleAction(parentOf(agent2.getAddress()), context));
		actionService2.processActions();

		// then
		assertThat(context.actionTarget, is((ISimpleAgent)aggregate2));
	}

	/**
	 * This action fails because there is an attempt to invoke it on the agent in the parent aggregate, but action is
	 * not wrapped in {@link PassToParentActionContext}. Correct usage is shown in
	 * {@link #testPassToParentInvokeOnAgentInParent()}
	 */
	@Test(expected = ActionTestException.class)
	public void testInvokeOnAgentInParent() {
		// given
		final DoNotPassToParentTestActionContext context = new DoNotPassToParentTestActionContext();

		// when
		actionService2.doAction(new SingleAction(unicastFor(agent.getAddress()), context));
		actionService2.processActions();
	}

	/**
	 * This action fails because there is an attempt to invoke action on parent aggregate, but action is not wrapped in
	 * {@link PassToParentActionContext} . Correct usage is shown in {@link #testPassToParentInvokeOnParentAggregate()}
	 */
	@Test(expected = ActionTestException.class)
	public void testInvokeOnParentAggregate() {
		// given
		final DoNotPassToParentTestActionContext context = new DoNotPassToParentTestActionContext();

		// when
		actionService2.doAction(new SingleAction(unicastFor(aggregate.getAddress()), context));
		actionService2.processActions();
	}

	@Test
	public void testPassToParentInvokeOnSelf() {
		// given
		final PassToParentTestActionContext context = new PassToParentTestActionContext();

		// when
		actionService2.doAction(new SingleAction(unicastFor(agent2.getAddress()), context));
		actionService2.processActions();

		// then
		assertThat(context.actionTarget, is(agent2));
	}

	@Test
	public void testPassToParentInvokeOnOwnAggregate() {
		// given
		final PassToParentTestActionContext context = new PassToParentTestActionContext();

		// when
		actionService2.doAction(new SingleAction(new ParentAgentAddressSelector(agent2.getAddress()), context));
		actionService2.processActions();

		// then
		assertThat(context.actionTarget, is((ISimpleAgent)aggregate2));
	}

	@Test
	public void testPassToParentInvokeOnAgentInParent() {
		// given
		final PassToParentTestActionContext context = new PassToParentTestActionContext();
		final Action action = new SingleAction(unicastFor(agent.getAddress()), context);
		final PassToParentActionContext outerContext = new PassToParentActionContext(agent2.getAddress(), action);

		// when
		actionService2.doAction(new SingleAction(parentOf(agent2.getAddress()), outerContext));
		actionService2.processActions();
		actionService.processActions();

		// then
		assertThat(context.actionTarget, is(agent));
	}

	@Test
	public void testPassToParentInvokeOnParentAggregate() {
		// given
		final PassToParentTestActionContext context = new PassToParentTestActionContext();
		final SingleAction action = new SingleAction(parentOf(aggregate2.getAddress()), context);
		final PassToParentActionContext outerContext = new PassToParentActionContext(agent2.getAddress(), action);

		// when
		actionService2.doAction(new SingleAction(parentOf(agent2.getAddress()), outerContext));
		actionService2.processActions();
		actionService.processActions();

		// then
		assertThat(context.actionTarget, is((ISimpleAgent)aggregate));
	}

	/**
	 * This action fails because there is an attempt to perform action on parent aggregate, but the aggregate has no
	 * agent environment.
	 */
	@Test(expected = ActionTestException.class)
	public void testPassToParentInvokeOnUnavailableParentAggregate() {
		// given
		aggregate.setAgentEnvironment(null);
		final PassToParentTestActionContext context = new PassToParentTestActionContext();
		final SingleAction action = new SingleAction(parentOf(agent.getAddress()), context);
		final PassToParentActionContext outerContext = new PassToParentActionContext(agent.getAddress(), action);

		// when
		actionService.doAction(new SingleAction(new ParentAgentAddressSelector(agent.getAddress()), outerContext));
		actionService.processActions();
	}
}
