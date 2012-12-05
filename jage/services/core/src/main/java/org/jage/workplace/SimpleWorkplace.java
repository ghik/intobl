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
 * $Id: SimpleWorkplace.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.workplace;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.IAgentEnvironment;
import org.jage.agent.ISimpleAgent;
import org.jage.agent.ISimpleAgentEnvironment;
import org.jage.agent.SimpleAggregate;
import org.jage.platform.component.exception.ComponentException;
import org.jage.property.PropertyField;

import static com.google.common.base.Preconditions.checkState;

/**
 * This is a simplest possible workplace. It does not provide any means to communicate with other workplaces.
 *
 * @author AGH AgE Team
 */
public class SimpleWorkplace extends SimpleAggregate implements Workplace<ISimpleAgent>, Runnable {

	/**
	 * The name of the step property used in "stepped" workplaces.
	 */
	public static final String STEP_PROPERTY_NAME = "step";

	private static final long serialVersionUID = 5L;

	private static final long PAUSE_DELAY = 1000;

	private static final Logger log = LoggerFactory.getLogger(SimpleWorkplace.class);

	@GuardedBy("stateMonitor")
	@Nonnull
	private State state = State.STOPPED;

	@Nonnull
	private final Object stateMonitor = new Object();

	@Nullable
	private IWorkplaceEnvironment workplaceEnvironment;

	@PropertyField(propertyName = STEP_PROPERTY_NAME, isMonitorable = true)
	private long step = 0;

	public SimpleWorkplace(final AgentAddress address) {
		super(address);
	}

	@Inject
	public SimpleWorkplace(final AgentAddressSupplier supplier) {
		super(supplier);
	}

	@Override
	protected ISimpleAgentEnvironment getAgentEnvironment() {
		throw new IllegalOperationException("Agent environment is not applicable to workplace.");
	}

	@Override
	public void setAgentEnvironment(final IAgentEnvironment localEnvironment) {
		throw new IllegalOperationException("Agent environment is not applicable to workplace.");
	}

	@Override
	public void start() {
		log.info("{} is starting...", getAddress());
		synchronized (stateMonitor) {
			checkState(isStopped(), "Workplace has been already started.");
			final Thread thread = new Thread(this);
			thread.setName(getAddress().toString());
			// Won't work if in the future the runnable is being scheduled
			thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(final Thread t, final Throwable e) {
					log.error("Exception caught during run", e);
					setStopped();
				}
			});
			thread.start();
		}
	}

	@Override
	public void pause() {
		log.debug("{} asked to pause.", this);
		synchronized (stateMonitor) {
			checkState(isRunning(), "Workplace is not running.");
			state = State.PAUSED;
		}
	}

	@Override
	public void resume() {
		log.debug("{} asked to resume.", this);
		synchronized (stateMonitor) {
			checkState(isPaused(), "Workplace is not paused.");
			state = State.RUNNING;
		}
	}

	@Override
	public void stop() {
		log.debug("{} asked to stop.", this);
		synchronized (stateMonitor) {
			checkState(isRunning() || isPaused(), "Workplace is not running or paused.");
			state = State.STOPPING;
		}
	}

	@Override
	public boolean finish() throws ComponentException {
		synchronized (stateMonitor) {
			checkState(isStopped(), "Illegal use of finish. Must invoke stop() first.");
			super.finish();
			log.info("{} has been shut down.", this);
			return true;
		}
	}

	@Override
	public void run() {
		setRunning();
		log.info("{} has been started.", this);
		while (isRunning() || isPaused()) {
			if (isPaused()) {
				try {
					Thread.sleep(PAUSE_DELAY);
				} catch (final InterruptedException e) {
					log.info("Interrupted.", e);
				}
			} else {
				step();
			}
		}
		setStopped();
	}

	/**
	 * Performs step of all agents and processes events.
	 */
	@Override
	public void step() {
		withReadLock(new Runnable() {
			@Override
			public void run() {
				for (final ISimpleAgent agent : agents.values()) {
					agent.step();
				}
			}
		});

		getActionService().processActions();
		step++;
		notifyMonitorsForChangedProperties();
	}

	@Override
	public boolean isRunning() {
		return State.RUNNING.equals(state);
	}

	@Override
	public boolean isPaused() {
		return State.PAUSED.equals(state);
	}

	@Override
	public boolean isStopped() {
		return State.STOPPED.equals(state);
	}

	/**
	 * Gets the step.
	 *
	 * @return the step
	 */
	protected long getStep() {
		return step;
	}

	/**
	 * Sets the state of the workplace to "running".
	 */
	protected void setRunning() {
		synchronized (stateMonitor) {
			state = State.RUNNING;
		}
	}

	/**
	 * Sets the state of the workplace to "stopped".
	 */
	protected void setStopped() {
		log.info("{} stopped", this);
		synchronized (stateMonitor) {
			state = State.STOPPED;
		}
		getWorkplaceEnvironment().onWorkplaceStop(this);
	}

	@Override
	public void setWorkplaceEnvironment(final IWorkplaceEnvironment workplaceEnvironment) {
		if (workplaceEnvironment == null) {
			this.workplaceEnvironment = null;
		} else if (this.workplaceEnvironment == null) {
			this.workplaceEnvironment = workplaceEnvironment;

			getActionService().processActions();
			if (temporaryAgentsList != null) {
				addAll(temporaryAgentsList);
				temporaryAgentsList = null;
			}
		} else {
			throw new WorkplaceException(String.format("Environment in %s is already set.", this));
		}
	}

	/**
	 * Returns the environment of the workplace.
	 *
	 * @return the environment.
	 */
	@Nullable
	protected final IWorkplaceEnvironment getWorkplaceEnvironment() {
		return workplaceEnvironment;
	}

	/**
	 * Returns true, if the workplace has the environment.
	 *
	 * @return true, if the workplace has the environment.
	 */
	protected final boolean hasWorkplaceEnvironment() {
		return workplaceEnvironment != null;
	}

}
