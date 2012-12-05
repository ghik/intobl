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
 * Created: 2011-07-11
 * $Id: MapAgent.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.examples.configuration;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.address.agent.AgentAddress;
import org.jage.address.agent.AgentAddressSupplier;
import org.jage.agent.SimpleAgent;
import org.jage.platform.component.exception.ComponentException;

/**
 * This class provides a simple agent that presents how to obtain and handle maps from the instance provider.
 *
 * @author AGH AgE Team
 */
public class MapAgent extends SimpleAgent {

	private static final long serialVersionUID = 1L;

	private final Logger log = LoggerFactory.getLogger(MapAgent.class);

	@Inject
	private Map<String, ExampleClass> injectedMap;

	public MapAgent(final AgentAddress address) {
		super(address);
	}

	@Inject
	public MapAgent(final AgentAddressSupplier supplier) {
		super(supplier);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init() throws ComponentException {
		super.init();
		log.info("Initializing Map Simple Agent: {}", getAddress());

		// Print the injected map
		log.info("The instance was injected: {}", injectedMap);
		log.info("Values:");
		for (final Entry<String, ExampleClass> element : injectedMap.entrySet()) {
			log.info("Key: {}. Value: {}.", element.getKey(), element.getValue());
		}

		// Retrieve and print the "object-example" map
		final Map<String, ExampleClass> objectMap = (Map<String, ExampleClass>)instanceProvider
		        .getInstance("object-example");
		log.info("Obtained an instance of object-example: {}", objectMap);
		log.info("Values:");
		for (final Entry<String, ExampleClass> element : objectMap.entrySet()) {
			log.info("Key: {}. Value: {}.", element.getKey(), element.getValue());
		}

	}

	@Override
	public void step() {
		// Empty
	}

	@Override
	public boolean finish() {
		log.info("Finishing Map Agent: {}", getAddress());
		return true;
	}

}
