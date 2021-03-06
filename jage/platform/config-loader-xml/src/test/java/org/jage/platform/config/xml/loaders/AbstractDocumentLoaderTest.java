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
 * Created: 26-07-2012
 * $Id: AbstractDocumentLoaderTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.platform.config.xml.loaders;

import org.dom4j.Document;
import org.dom4j.util.NodeComparator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import org.jage.platform.component.definition.ConfigurationException;
import org.jage.platform.config.xml.util.DocumentBuilder;

import static org.jage.platform.config.xml.util.DocumentBuilder.emptyDocument;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Abstract base class for document loaders tests.
 *
 * @author AGH AgE Team
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDocumentLoaderTest<T extends AbstractDocumentLoader> {

	@Mock
	protected DocumentLoader delegate;

	protected final String path = "somePath";

	protected T loader;

	@Before
	public void setup() {
		loader = getLoader();
		loader.setDelegate(delegate);
	}

	@Test
	public void shouldIgnoresEmptyDocument() throws ConfigurationException {
		assertDocumentTransformation(emptyDocument(), emptyDocument());
	}

	protected abstract T getLoader();

	protected void tryDocumentTransformation(final DocumentBuilder original)
	        throws ConfigurationException {
		assertDocumentTransformation(original.build(), null);
	}

	protected void assertDocumentTransformation(final DocumentBuilder original, final DocumentBuilder expected)
	        throws ConfigurationException {
		assertDocumentTransformation(original.build(), expected.build());
	}

	protected void assertDocumentTransformation(final Document original, final Document expected)
	        throws ConfigurationException {
		checkArgument(original != expected);

		// given
		given(delegate.loadDocument(path)).willReturn(original);

		// when
		final Document actual = loader.loadDocument(path);

		// then
		assertEquals(0, new NodeComparator().compare(expected, actual));
	}
}
