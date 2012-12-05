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
 * Created: 2012-10-16
 * $Id: HazelcastInstanceFactory.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.communication.hazelcast;

import org.jage.annotation.ReturnValuesAreNonnullByDefault;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * A factory that keeps a node-wide instance of the Hazelcast.
 *
 * @author AGH AgE Team
 */
@ReturnValuesAreNonnullByDefault
public final class HazelcastInstanceFactory {
	private static HazelcastInstance hazelcastInstance;

	/**
	 * Returns the Hazelcast instance.
	 * 
	 * @return the Hazelcast instance.
	 */
	public static final HazelcastInstance getInstance() {
		if (hazelcastInstance == null) {
			Config config = new Config();
			config.setInstanceName("AgE");
			config.setProperty("hazelcast.logging.type", "slf4j");
			
			hazelcastInstance = Hazelcast.newHazelcastInstance(config);
		}
		return hazelcastInstance;
	}
}
