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
 * Created: 09-09-2010
 * $Id: IMutableComponentInstanceProvider.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.platform.component.provider;

import java.util.Collection;

import org.jage.platform.component.definition.IComponentDefinition;
import org.jage.platform.component.exception.ComponentException;

/**
 *
 * @author AGH AgE Team
 */
public interface IMutableComponentInstanceProvider extends IComponentInstanceProvider {
	
	/**
	 * Registers a component instance described by a given component definition. The component instance will be
	 * accessible by its implemented interfaces and the String ID (which is a name written in the definition).
	 * 
	 * @param compDefinition
	 *            a component definition which describes the component to be registered
	 */
	void addComponent(IComponentDefinition compDefinition);

	/**
	 * Registers a component implementation given by its class. The component instance will be accessible only by its
	 * implemented interfaces
	 * 
	 * @param compImplementation
	 *            a type (class) of component - the given type cannot describe an interface or an abstract class
	 */
	void addComponent(Class<?> compImplementation);

	/**
	 * Registers a component implementation with the given String key. The component instance will be accessible by its
	 * implemented interfaces and the given String key.
	 * 
	 * @param key
	 *            a string key for the component
	 * @param compType
	 *            a type of component - the given type cannot describe an interface or an abstract class
	 */
	void addComponent(String key, Class<?> compType);

	/**
	 * Registers a given component instance. It will be accessible only by its implemented interfaces
	 * 
	 * @param compInstance
	 *            a component instance to be registered
	 */
	void addComponentInstance(Object compInstance);
	
	void verify() throws ComponentException;
	
	void reconfigure(Collection<IComponentDefinition> defs);
}
