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
 * Created: 2012-08-21
 * $Id: LifecycleMessagesTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.lifecycle;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jage.address.component.ComponentAddress;

import static org.jage.lifecycle.LifecycleMessages.createExit;
import static org.jage.lifecycle.LifecycleMessages.createHasInteractiveControllerNotification;
import static org.jage.lifecycle.LifecycleMessages.createNoInteractiveControllerNotification;
import static org.jage.lifecycle.LifecycleMessages.createStart;

/**
 * Tests for the {@link LifecycleMessages} class.
 * 
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public class LifecycleMessagesTest {

	@Mock
	private ComponentAddress componentAddress;

	@Test
	public void testCreateNoInteractiveControllerNotification() {
		// when
		final LifecycleMessage message = createNoInteractiveControllerNotification(componentAddress);

		// then
		assertThat(message, is(notNullValue()));
		assertThat(message.getCommand(), is(LifecycleHeader.LifecycleCommand.NOTIFY));
		assertThat(message.getPayload(), is(instanceOf(Map.class)));
		assertThat((Map<String, Boolean>)message.getPayload(), hasEntry("interactiveController", false));
	}

	@Test
	public void testCreateHasInteractiveControllerNotification() {
		// when
		final LifecycleMessage message = createHasInteractiveControllerNotification(componentAddress);

		// then
		assertThat(message, is(notNullValue()));
		assertThat(message.getCommand(), is(LifecycleHeader.LifecycleCommand.NOTIFY));
		assertThat(message.getPayload(), is(instanceOf(Map.class)));
		assertThat((Map<String, Boolean>)message.getPayload(), hasEntry("interactiveController", true));
	}

	@Test
	public void testCreateStart() {
		// when
		final LifecycleMessage message = createStart(componentAddress);

		// then
		assertThat(message, is(notNullValue()));
		assertThat(message.getCommand(), is(LifecycleHeader.LifecycleCommand.START));
		assertThat(message.getPayload(), is(nullValue()));
	}

	@Test
	public void testCreateExit() {
		// when
		final LifecycleMessage message = createExit(componentAddress);

		// then
		assertThat(message, is(notNullValue()));
		assertThat(message.getCommand(), is(LifecycleHeader.LifecycleCommand.EXIT));
		assertThat(message.getPayload(), is(nullValue()));
	}
}
