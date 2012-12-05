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
 * Created: 2011-04-09
 * $Id: AggregateActionServiceExecuteActionsTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.agent;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.jage.action.ComplexAction;
import org.jage.action.SingleAction;
import org.jage.action.testHelpers.HelperTestAggregateActionService;
import org.jage.action.testHelpers.TracingActionContext;
import org.jage.address.agent.AgentAddress;
import org.jage.address.selector.BroadUnusedSelector;
import org.jage.address.selector.UnicastSelector;
import org.jage.platform.component.provider.IComponentInstanceProvider;

import static org.jage.address.selector.Selectors.getOnlyAddress;
import static org.jage.utils.AgentTestUtils.createSimpleAgentWithoutStep;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Tests for the {@link AggregateActionService} class: the action execution.
 *
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class AggregateActionServiceExecuteActionsTest {

	private static final int AGENT_COUNT = 10;

	private final SimpleAggregate aggregate = new SimpleAggregate(mock(AgentAddress.class));

	private final HelperTestAggregateActionService actionService = new HelperTestAggregateActionService();

	private ISimpleAgent[] agents;

	private AgentAddress[] addresses;

	private final List<UnicastSelector<AgentAddress>> unicasts = newLinkedList();

	private final BroadUnusedSelector<AgentAddress> broadcast = new BroadUnusedSelector<AgentAddress>();

	private ComplexAction action;

	private TracingActionContext context;

	@Mock
	private IComponentInstanceProvider componentInstanceProvider;

	@Before
	public void setUp() throws Exception {
		// Configure agents
		actionService.setInstanceProvider(componentInstanceProvider);
		actionService.setAggregate(aggregate);

		agents = new SimpleAgent[AGENT_COUNT];
		addresses = new AgentAddress[AGENT_COUNT];

		for (int i = 0; i < AGENT_COUNT; i++) {
			final SimpleAgent agent = createSimpleAgentWithoutStep();
			agent.setInstanceProvider(componentInstanceProvider);
			agent.init();
			agents[i] = agent;
			addresses[i] = agent.getAddress();
		}

		aggregate.addAll(Arrays.asList(agents));

		createAction();
	}

	public void createAction() {
		unicasts.add(new UnicastSelector<AgentAddress>(addresses[0]));
		unicasts.add(new UnicastSelector<AgentAddress>(addresses[1]));
		unicasts.add(new UnicastSelector<AgentAddress>(addresses[2]));

		context = new TracingActionContext();
		final SingleAction sa1 = new SingleAction(unicasts.get(0), context, "c1Action");
		final SingleAction sa2 = new SingleAction(unicasts.get(1), context, "c2Action");
		final SingleAction sa3 = new SingleAction(unicasts.get(2), context, "c3Action");
		final SingleAction sa4 = new SingleAction(broadcast, context, "c4Action");

		action = new ComplexAction();
		action.addChild(sa1);
		action.addChild(sa2);
		action.addChild(sa3);
		action.addChild(sa4);
	}

	@Test
	public void processActionValidationTest() {
		// when
		final Collection<AgentAddress> used = actionService.initializeAction(action);

		// then
		assertThat(used, is(notNullValue()));
		assertEquals(unicasts.size(), used.size());

		for (final UnicastSelector<AgentAddress> unicast : unicasts) {
			assertThat(used, hasItem(getOnlyAddress(unicast)));
		}
	}

	@Test
	public void processActionAddressGenerationTest() {
		// given
		final Collection<AgentAddress> used = actionService.initializeAction(action);

		// when
		actionService.initializeActionTargets(action, Arrays.asList(addresses), used);

		// then
		for (final AgentAddress address : addresses) {
			assertThat(used.contains(address) != broadcast.selected(address), is(true));
		}
	}

	@Test
	public void processActionPerformTest() {
		// given
		final Collection<AgentAddress> used = actionService.initializeAction(action);
		actionService.initializeActionTargets(action, Arrays.asList(addresses), used);

		// when
		actionService.executeAction(action);

		// then
		assertThat(context.trace, is("1234444444"));
	}

}
