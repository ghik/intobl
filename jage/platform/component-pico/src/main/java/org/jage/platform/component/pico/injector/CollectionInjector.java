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
 * Created: 2010-09-14
 * $Id: CollectionInjector.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.platform.component.pico.injector;

import java.util.Collection;
import java.util.List;

import org.picocontainer.Parameter;
import org.picocontainer.Parameter.Resolver;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jage.platform.component.definition.CollectionDefinition;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Injector implementation for collections.
 *
 * @param <T>
 *            the type of components this injector can create
 *
 * @author AGH AgE Team
 */
public final class CollectionInjector<T> extends AbstractInjector<T> {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(CollectionInjector.class);

	private final CollectionDefinition definition;

	private final List<Parameter> parameters;

	/**
	 * Creates an CollectionInjector using a given collection definition.
	 *
	 * @param definition
	 *            the collection definition to use
	 */
	public CollectionInjector(final CollectionDefinition definition) {
		super(definition);
		this.definition = checkNotNull(definition);
		parameters = Parameters.fromList(definition.getItems());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public T doCreate(final PicoContainer container) throws PicoCompositionException {
		log.trace("Constructing a collection from definition {}.", definition);
		try {
			final Collection instance = (Collection)definition.getType().newInstance();
			for (Parameter parameter : parameters) {
				Object value = doResolve(parameter, container).resolveInstance();
				instance.add(checkNotNull(value));
			}
			return (T)instance;
		} catch (final InstantiationException e) {
			throw wrappedException(e);
		} catch (final IllegalAccessException e) {
			throw wrappedException(e);
		}
	}

	private Resolver doResolve(final Parameter parameter, final PicoContainer container) {
		return parameter.resolve(container, this, null, definition.getElementsType(), null, false, null);
	}

	@Override
	public void doVerify(final PicoContainer container) throws PicoCompositionException {
		log.trace("Verifying CollectionInjector for definition {}.", definition);
		for (Parameter parameter : parameters) {
			parameter.verify(container, this, definition.getElementsType(), null, false, null);
		}
	}

	@Override
	public String getDescriptor() {
		return "Collection injector";
	}
}
