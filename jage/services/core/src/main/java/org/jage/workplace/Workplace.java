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
 * $Id: Workplace.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.workplace;

import org.jage.agent.IAgent;
import org.jage.agent.IAggregate;
import org.jage.agent.ThreadedAgent;
import org.jage.platform.component.exception.ComponentException;

/**
 * The component that carries out simulation. It is responsible for agents, their life and activities.
 * 
 * @param <C>
 *            a lower bound of agents located in this aggregate.
 * 
 * @author AGH AgE Team
 */
public interface Workplace<C extends IAgent> extends ThreadedAgent, IAggregate<C> {

	/**
	 * States of workplaces.
	 */
	public enum State {
		/**
		 * A workplace is running and performing computation.
		 */
		RUNNING,
		/**
		 * A workplace is stopping - it will transition into STOPPED state.
		 */
		STOPPING,
		/**
		 * A workplace is paused temporarily.
		 */
		PAUSED,
		/**
		 * The workplace is stopped.
		 */
		STOPPED
	}

	/**
	 * Sets new workplace environment to this workplace.
	 * 
	 * @param workplaceEnvironment
	 *            A workplace environment to set.
	 * 
	 * @throws WorkplaceException
	 *             when environment is already set.
	 */
	void setWorkplaceEnvironment(IWorkplaceEnvironment workplaceEnvironment);

	/**
	 * Initializes workplace. After calling this method workplace is ready to start or can be moved to another workplace
	 * manager.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	void init() throws ComponentException;

	/**
	 * Starts the workplace.
	 */
	void start();

	/**
	 * Pauses the workplace.
	 */
	void pause();

	/**
	 * Resumes the paused workplace/
	 */
	void resume();

	/**
	 * Stops the workplace. After calling this method workplace can be restarted - using the {@link #start} method.
	 */
	void stop();

	/**
	 * Finishes workplace. After calling this method workplace cannot be used anymore.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	boolean finish() throws ComponentException;

}
