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
 * Created: 2010-06-23
 * $Id: ChangesNotifierArrayMonitoringStrategy.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.property;

import java.lang.reflect.Array;

import org.jage.event.AbstractEvent;
import org.jage.event.ObjectChangedEvent;
import org.jage.monitor.IChangesNotifier;
import org.jage.monitor.IChangesNotifierMonitor;

/**
 * Monitoring strategy for array of objects that implement IChangesNotifier interface. This class is used by Property
 * class.
 *
 * @author AGH AgE Team
 */
public class ChangesNotifierArrayMonitoringStrategy extends PropertyValueMonitoringStrategy {

	private Object monitoredPropertyValue;

	private ChangesNotifierMonitor[] monitors;

	/**
	 * Constructor.
	 *
	 * @param property
	 *            property that uses this strategy
	 */
	public ChangesNotifierArrayMonitoringStrategy(Property property) {
		super(property);
		monitoredPropertyValue = property.getValue();
		attachMonitors();
	}

	/**
	 * Informs the strategy that property value has been changed.
	 *
	 * @param newValue
	 *            new property value.
	 */
	@Override
	public void propertyValueChanged(Object newValue) {
		if (newValue != getOldValue()) {
			detachMonitors();
			monitoredPropertyValue = newValue;
			attachMonitors();
			setOldValue(newValue);
		}
	}

	/**
	 * Attaches monitors to all elements in the property value.
	 */
	private void attachMonitors() {
		if (monitoredPropertyValue != null) {
			int length = Array.getLength(monitoredPropertyValue);
			monitors = new ChangesNotifierMonitor[length];
			for (int i = 0; i < length; i++) {
				IChangesNotifier changesNotifier = (IChangesNotifier)Array.get(monitoredPropertyValue, i);
				if (changesNotifier != null) {
					monitors[i] = new ChangesNotifierMonitor(i);
					changesNotifier.addMonitor(monitors[i]);
				}
			}
		}
	}

	/**
	 * Unregisters monitors from all elements in the property value.
	 */
	private void detachMonitors() {
		if (monitoredPropertyValue != null) {
			int length = Array.getLength(monitoredPropertyValue);
			for (int i = 0; i < length; i++) {
				IChangesNotifier changesNotifier = (IChangesNotifier)Array.get(monitoredPropertyValue, i);
				if (changesNotifier != null && monitors[i] != null) {
					changesNotifier.removeMonitor(monitors[i]);
				}
			}
		}
	}

	private void arrayElementChanged() {
		getProperty().notifyMonitors(monitoredPropertyValue);
	}

	private void arrayElementDeleted(int arrayIndex) {
		IChangesNotifier notifier = (IChangesNotifier)Array.get(monitoredPropertyValue, arrayIndex);
		notifier.removeMonitor(monitors[arrayIndex]);
		monitors[arrayIndex] = null;
	}

	/**
	 * A private implementation of {@link IChangesNotifierMonitor}.
	 *
	 * @author AGH AgE Team
	 */
	private class ChangesNotifierMonitor implements IChangesNotifierMonitor {

		private int indexInArray;

		public ChangesNotifierMonitor(int indexInArray) {
			this.indexInArray = indexInArray;
		}

		public void objectChanged(Object sender, ObjectChangedEvent event) {
			arrayElementChanged();
		}

		public void ownerDeleted(AbstractEvent event) {
			arrayElementDeleted(indexInArray);
		}
	}
}
