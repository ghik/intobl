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
 * $Id: LifecycleHeader.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.lifecycle;

import javax.annotation.Nonnull;

import org.jage.address.component.ComponentAddress;
import org.jage.address.selector.IAddressSelector;
import org.jage.communication.message.Header;
import org.jage.services.core.LifecycleManager;

import static com.google.common.base.Objects.toStringHelper;

/**
 * A header type used by {@link LifecycleManager} in {@link LifecycleMessage}. This header adds a single parameter:
 * option, to usual header fields.
 * 
 * @author AGH AgE Team
 */
public final class LifecycleHeader extends Header<ComponentAddress> {

	private static final long serialVersionUID = 1L;

	private final LifecycleCommand command;

	/**
	 * Creates a new header.
	 * 
	 * @param command
	 *            a command (message type).
	 * @param senderAddress
	 *            a sender address.
	 * @param receiverSelector
	 *            a selector that selects targets.
	 */
	public LifecycleHeader(final LifecycleCommand command, final ComponentAddress senderAddress,
	        final IAddressSelector<ComponentAddress> receiverSelector) {
		super(senderAddress, receiverSelector);
		this.command = command;
	}

	/**
	 * A list of available commands (message types).
	 * 
	 * @author AGH AgE Team
	 */
	public static enum LifecycleCommand {
		/**
		 * Start the computation.
		 */
		START,
		/**
		 * Pause the computation.
		 */
		PAUSE,
		/**
		 * Stop the computation.
		 */
		STOP,
		/**
		 * Node failure.
		 */
		FAIL,
		/**
		 * Notifications.
		 */
		NOTIFY,
		/**
		 * Shutdown the environment.
		 */
		EXIT;
	}

	/**
	 * Returns the command that this header represents.
	 * 
	 * @return a command that this header represents.
	 */
	@Nonnull
	public LifecycleCommand getCommand() {
		return command;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("command", command).add("sender", getSenderAddress())
		        .add("receivers", getReceiverSelector()).toString();
	}
}
