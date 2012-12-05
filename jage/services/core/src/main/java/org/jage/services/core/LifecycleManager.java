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
 * $Id: LifecycleManager.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.services.core;

import java.util.List;

import javax.annotation.Nullable;

import org.jage.platform.component.definition.IComponentDefinition;

import com.google.common.eventbus.EventBus;

/**
 * A service that manages the lifecycle of every AgE node.
 * 
 * @author AGH AgE Team
 */
public interface LifecycleManager {

	/**
	 * Called by the configuration service when a new configuration is available.
	 * 
	 * @param alreadyInjected
	 *            true, if the configuration is already instantiated (e.g., it was included in the node configuration).
	 * @param configuration
	 *            a configuration to use ({@code null} if the configuration was already injected).
	 */
	void onNewComputationConfiguration(boolean alreadyInjected, @Nullable List<IComponentDefinition> configuration);

	/**
	 * Called when the stop condition has been fulfilled. The stop condition must guarantee that it will be called at
	 * most once.
	 */
	void onStopConditionFulfilled();

	/**
	 * Registers a listener to lifecycle events.
	 * 
	 * @param listener
	 *            a listener.
	 * @see EventBus#register(Object)
	 */
	void register(Object listener);

	/**
	 * Unregisters a listener from lifecycle events.
	 * 
	 * @param listener
	 *            a listener.
	 * @see EventBus#unregister(Object)
	 */
	void unregister(Object listener);
}
