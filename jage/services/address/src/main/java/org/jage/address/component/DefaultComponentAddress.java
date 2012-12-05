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
 * Created: 2010-04-09
 * $Id: DefaultComponentAddress.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.address.component;

import javax.annotation.concurrent.Immutable;

import org.jage.address.AbstractAddress;
import org.jage.address.node.NodeAddress;
import org.jage.address.node.UnspecifiedNodeAddress;

/**
 * This class provides a default implementation of {@link ComponentAddress}.
 *
 * @author AGH AgE Team
 */
@Immutable
public class DefaultComponentAddress extends AbstractAddress implements ComponentAddress {

	private static final long serialVersionUID = -5639158900906333574L;

	public DefaultComponentAddress(final String componentName) {
		this(componentName, new UnspecifiedNodeAddress());
	}

	public DefaultComponentAddress(final String componentName, final NodeAddress nodeAddress) {
		super(componentName, nodeAddress);
	}
}
