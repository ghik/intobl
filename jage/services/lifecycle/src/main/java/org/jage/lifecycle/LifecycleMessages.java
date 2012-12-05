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
 * $Id: LifecycleMessages.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.lifecycle;

import org.jage.address.component.ComponentAddress;
import org.jage.address.selector.BroadcastSelector;
import org.jage.annotation.ReturnValuesAreNonnullByDefault;
import org.jage.lifecycle.LifecycleHeader.LifecycleCommand;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Utilities for working with lifecycle messages.
 *
 * @author AGH AgE Team
 */
@ReturnValuesAreNonnullByDefault
public final class LifecycleMessages {
	private LifecycleMessages() {
		// Empty
	}

	/**
	 * Creates an instance of the <strong>NOTIFY</strong> {@link LifecycleMessage}.
	 *
	 * @param sender
	 * 		a sender of the message (LifecycleManager).
	 * @return a new NOTIFY LifecycleMessage.
	 */
	public static LifecycleMessage createNoInteractiveControllerNotification(final ComponentAddress sender) {
		final LifecycleHeader header =
				new LifecycleHeader(LifecycleCommand.NOTIFY, sender, BroadcastSelector.<ComponentAddress>create());
		final Builder<String, Boolean> builder = ImmutableMap.builder();
		builder.put("interactiveController", false);
		return new LifecycleMessage(header, builder.build());
	}

	/**
	 * Creates an instance of the <strong>NOTIFY</strong> {@link LifecycleMessage}.
	 *
	 * @param sender
	 * 		a sender of the message (LifecycleManager).
	 * @return a new NOTIFY LifecycleMessage.
	 */
	public static LifecycleMessage createHasInteractiveControllerNotification(final ComponentAddress sender) {
		final LifecycleHeader header =
				new LifecycleHeader(LifecycleCommand.NOTIFY, sender, BroadcastSelector.<ComponentAddress>create());
		final Builder<String, Boolean> builder = ImmutableMap.builder();
		builder.put("interactiveController", true);
		return new LifecycleMessage(header, builder.build());
	}

	/**
	 * Creates an instance of the <strong>START</strong> {@link LifecycleMessage}.
	 *
	 * @param sender
	 * 		a sender of the message (LifecycleManager).
	 * @return a new START LifecycleMessage.
	 */
	public static LifecycleMessage createStart(final ComponentAddress sender) {
		final LifecycleHeader header =
				new LifecycleHeader(LifecycleCommand.START, sender, BroadcastSelector.<ComponentAddress>create());
		return new LifecycleMessage(header, null);
	}

	/**
	 * Creates an instance of the <strong>EXIT</strong> {@link LifecycleMessage}.
	 *
	 * @param sender
	 * 		a sender of the message (LifecycleManager).
	 * @return a new EXIT LifecycleMessage.
	 */
	public static LifecycleMessage createExit(final ComponentAddress sender) {
		final LifecycleHeader header =
				new LifecycleHeader(LifecycleCommand.EXIT, sender, BroadcastSelector.<ComponentAddress>create());
		return new LifecycleMessage(header, null);
	}
}
