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
 * $Id: FixedStepCountStopCondition.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.workplace;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.event.AbstractEvent;
import org.jage.event.PropertyEvent;
import org.jage.platform.component.exception.ComponentException;
import org.jage.property.InvalidPropertyOperationException;
import org.jage.property.InvalidPropertyPathException;
import org.jage.property.monitors.AbstractPropertyMonitor;
import org.jage.property.monitors.DefaultPropertyMonitorRule;
import org.jage.query.PropertyContainerCollectionQuery;
import org.jage.services.core.CoreComponent;
import org.jage.services.core.LifecycleManager;
import org.jage.workplace.manager.WorkplaceManager;

import static org.jage.query.ValueFilters.lessThan;
import static org.jage.query.ValueSelectors.property;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Stop condition which stops workplaces when all of them perform fixed steps
 * count. It gets in constructor number of steps after which workplaces are
 * stopped. In no number is given the default value is set.
 * <p>
 * 
 * Important: with the version 2.6 semantics of this stop condition changed: now
 * <emph>all</emph> of workplaces must pass at least the provided number of
 * steps for the computation to stop.
 * 
 * @author AGH AgE Team
 */
public class FixedStepCountStopCondition extends AbstractPropertyMonitor implements IStopCondition {

	private static Logger log = LoggerFactory.getLogger(FixedStepCountStopCondition.class);

	private static final long DEFAULT_STEP_COUNT = 10;

	@Inject
	private WorkplaceManager workplaceManager;

	@Inject
	private LifecycleManager lifecycleManager;

	private long stepCount = DEFAULT_STEP_COUNT;

	private final AtomicBoolean alreadySatisfied = new AtomicBoolean(false);

	/**
	 * Creates a new step counting condition with the default maximum step
	 * count.
	 */
	@Inject
	public FixedStepCountStopCondition() {
		log.info("Fixed step stop condition created with default step set to: {}", stepCount);
	}

	/**
	 * Creates a new step counting condition with the provided maximum step
	 * count.
	 * 
	 * @param stepCount
	 *            a number of steps before this stop condition is satisfied,
	 *            must be greater than zero.
	 */
	@Inject
	public FixedStepCountStopCondition(final Long stepCount) {
		if (stepCount != null) {
			checkArgument(stepCount > 0,
					"FixedStepCountStopCondition: number of steps cannot be equal or less then zero. Given value: %s.",
					stepCount);
			this.stepCount = stepCount;
		}
		log.info("Fixed step stop condition created with step set to: {}.", this.stepCount);

	}

	@Override
	public void onCoreComponentStarting(final CoreComponent coreComponent) {
		assert coreComponent.equals(workplaceManager);
		try {
			for (final Workplace<?> workplace : workplaceManager.getWorkplaces()) {
				workplace.getProperty(SimpleWorkplace.STEP_PROPERTY_NAME).addMonitor(this,
						new DefaultPropertyMonitorRule());
			}
		} catch (final InvalidPropertyOperationException e) {
			log.error("Step property is set to read-only while it shouldn't", e);
			throw new RuntimeException("'Step' property is read-only.", e);
		} catch (final InvalidPropertyPathException e) {
			log.error("Cannot find step property in workplace.", e);
			throw new RuntimeException("Cannot find a 'step' property in a workplace.", e);
		}

	}

	@Override
	public void onCoreComponentStopped(final CoreComponent coreComponent) {
		// do nothing
	}

	@Override
	public void init() throws ComponentException {
		if (workplaceManager == null) {
			throw new ComponentException("Workplace manager has not been injected. Is it configured?");
		}
		workplaceManager.registerListener(this);
	}

	@Override
	public boolean finish() throws ComponentException {
		workplaceManager.unregisterListener(this);
		return true;
	}

	@Override
	protected void propertyChanged(final PropertyEvent event) {
		final Object value = event.getProperty().getValue();
		checkArgument(value instanceof Long, "Wrong property type. Should be long, but actual type is %s.",
				value.getClass());

		if (!alreadySatisfied.get() && shouldStop()) {
			log.info("The stop condition has been satisfied.");
			alreadySatisfied.set(true);
			lifecycleManager.onStopConditionFulfilled();
		}
	}

	@Override
	public void ownerDeleted(final AbstractEvent event) {
		// ignore
	}

	private boolean shouldStop() {
		final PropertyContainerCollectionQuery<Workplace, Long> query = new PropertyContainerCollectionQuery<Workplace, Long>(
				Workplace.class);

		final Collection<Long> results = query.matching(SimpleWorkplace.STEP_PROPERTY_NAME, lessThan(stepCount))
				.select(property(SimpleWorkplace.STEP_PROPERTY_NAME)).execute(workplaceManager.getWorkplaces());
		return results.isEmpty();
	}

}
