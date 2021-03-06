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
 * Created: 2009-05-18
 * $Id: CreateNewAgentActionTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.agent;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.jage.action.AgentActions;
import org.jage.action.ComplexAction;
import org.jage.action.SingleAction;
import org.jage.action.testHelpers.HelperTestAggregateActionService;
import org.jage.action.testHelpers.TracingActionContext;
import org.jage.address.agent.AgentAddress;
import org.jage.platform.component.provider.IComponentInstanceProvider;

import static org.jage.address.selector.Selectors.unicastFor;
import static org.jage.utils.AgentTestUtils.createMockAgentWithAddress;

/**
 * Tests for the {@link AggregateActionService} class: the creation of a new agent.
 *
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateNewAgentActionTest {

	private final SimpleAggregate aggregate = new SimpleAggregate(mock(AgentAddress.class));

	private final HelperTestAggregateActionService actionService = new HelperTestAggregateActionService();

	private final ISimpleAgent agent = createMockAgentWithAddress();

	@Mock
	private IComponentInstanceProvider componentInstanceProvider;

	@Before
	public void setUp() throws Exception {
		aggregate.setInstanceProvider(componentInstanceProvider);
		aggregate.add(agent);
		actionService.setAggregate(aggregate);
		actionService.setInstanceProvider(componentInstanceProvider);
	}

	@Test
	public void createNewAgentActionTest() throws AgentException {
		// given
		final ISimpleAgent newAgent = createMockAgentWithAddress();
		final ComplexAction action = new ComplexAction();
		final SingleAction addAction = AgentActions.addToParent(agent, newAgent);

		action.addChild(addAction);

		final TracingActionContext tracingContext = new TracingActionContext();
		action.addChild(new SingleAction(unicastFor(newAgent.getAddress()), tracingContext,
		        "c1Action"));
		action.addChild(new SingleAction(unicastFor(newAgent.getAddress()), tracingContext,
		        "c2Action"));

		// when
		actionService.doAction(action);
		actionService.processActions();
		final List<ISimpleAgent> agents = aggregate.getAgents();

		// then
		assertThat(tracingContext.trace, is("12"));
		assertThat(agents, hasItem(newAgent));
	}

}
