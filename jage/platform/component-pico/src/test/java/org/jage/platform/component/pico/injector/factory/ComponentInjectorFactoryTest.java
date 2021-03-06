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
 * Created: 2012-03-15
 * $Id: ComponentInjectorFactoryTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.platform.component.pico.injector.factory;

import org.junit.Test;
import org.picocontainer.ComponentAdapter;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.jage.platform.component.definition.ComponentDefinition;
import org.jage.platform.component.pico.injector.ComponentInjector;

/**
 * Tests for ComponentInjectorFactory.
 *
 * @author AGH AgE Team
 */
public class ComponentInjectorFactoryTest {

	private final ComponentInjectorFactory factory = new ComponentInjectorFactory();

	@Test(expected = NullPointerException.class)
	public void shouldThrowNPEForNullDefinition() {
		// when
		factory.createAdapter(null);
	}

	@Test
	public void shouldCreateComponentInjectors() {
		// given
		final ComponentDefinition definition = new ComponentDefinition("any", Object.class, false);

		// when
		final ComponentAdapter<Object> adapter = factory.createAdapter(definition);

		// then
		assertThat(adapter, is(instanceOf(ComponentInjector.class)));
	}
}
