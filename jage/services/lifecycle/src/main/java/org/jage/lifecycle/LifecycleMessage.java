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
 * Created: 2012-08-21
 * $Id: LifecycleMessage.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.lifecycle;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.jage.address.component.ComponentAddress;
import org.jage.annotation.ReturnValuesAreNonnullByDefault;
import org.jage.communication.message.Message;
import org.jage.lifecycle.LifecycleHeader.LifecycleCommand;
import org.jage.services.core.LifecycleManager;

/**
 * A message type used by {@link LifecycleManager}. It consists of a
 * 
 * @author AGH AgE Team
 */
@ReturnValuesAreNonnullByDefault
public final class LifecycleMessage extends Message<ComponentAddress, Serializable> {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new LifecycleMessage.
	 * 
	 * @param header
	 *            A header that contains metadata for this message.
	 * @param payload
	 *            A payload to transport.
	 */
	public LifecycleMessage(final LifecycleHeader header, @Nullable final Serializable payload) {
		super(header, payload);
	}

	@Override
	public LifecycleHeader getHeader() {
		return (LifecycleHeader)super.getHeader();
	}

	/**
	 * Returns the command that thsi message represents.
	 * 
	 * @return a command.
	 */
	public LifecycleCommand getCommand() {
		return getHeader().getCommand();
	}

}
