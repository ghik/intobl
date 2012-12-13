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

package org.jage.emas.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.PrintStream;

import javax.inject.Inject;

import org.jage.event.AbstractEvent;
import org.jage.event.PropertyEvent;
import org.jage.platform.component.IStatefulComponent;
import org.jage.platform.component.exception.ComponentException;
import org.jage.property.InvalidPropertyOperationException;
import org.jage.property.InvalidPropertyPathException;
import org.jage.property.monitors.AbstractPropertyMonitor;
import org.jage.property.monitors.DefaultPropertyMonitorRule;
import org.jage.services.core.LifecycleManager;
import org.jage.workplace.SimpleWorkplace;
import org.jage.workplace.Workplace;
import org.jage.workplace.manager.WorkplaceManager;

public class BestFitnessTracer extends AbstractPropertyMonitor implements IStatefulComponent {

	private long steps;
	private String filename;

	private volatile double bestFitness = -Double.MAX_VALUE;

	@Inject
	private LifecycleManager lifecycleManager;

	@Inject
	private WorkplaceManager workplaceManager;

	private PrintStream ps;

	public void setSteps(long steps) {
		this.steps = steps;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void updateBestFitness(double islandBestFitness) {
		if (islandBestFitness > bestFitness) {
			bestFitness = islandBestFitness;
		}
	}

	@Override
	public void ownerDeleted(AbstractEvent event) {
	}

	@Override
	protected void propertyChanged(PropertyEvent event) {
		final Object value = event.getProperty().getValue();
		checkArgument(value instanceof Long, "Wrong property type. Should be long, but actual type is %s.",
				value.getClass());

		long step = (Long) value;
		if (step <= steps) {
			ps.printf("%s,%s\n", step - 1, bestFitness);
		}
	}

	@Override
	public void init() throws ComponentException {
		try {
			for (final Workplace<?> workplace : workplaceManager.getWorkplaces()) {
				workplace.getProperty(SimpleWorkplace.STEP_PROPERTY_NAME).addMonitor(this,
						new DefaultPropertyMonitorRule());
			}
		} catch (final InvalidPropertyOperationException e) {
			throw new RuntimeException("'Step' property is read-only.", e);
		} catch (final InvalidPropertyPathException e) {
			throw new RuntimeException("Cannot find a 'step' property in a workplace.", e);
		}

		try {
			ps = new PrintStream(filename);
		} catch (Exception e) {
			throw new ComponentException(e);
		}
	}

	@Override
	public boolean finish() throws ComponentException {
		try {
			System.out.println(new File(filename).getAbsolutePath());
			ps.close();
		} catch (Exception e) {
			throw new ComponentException(e);
		}

		return true;
	}

}
