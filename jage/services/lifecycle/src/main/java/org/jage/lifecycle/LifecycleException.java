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
 * Created: 2012-08-20
 * $Id: LifecycleException.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.lifecycle;

import org.jage.exception.AgeException;

/**
 * An exception type thrown by the LifecycleManager for unrecoverable lifecycle errors. It usually leads to the node
 * failure and shutdown.
 *
 * @author AGH AgE Team
 */
public class LifecycleException extends AgeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new runtime exception with the specified detail message. The cause is not initialized, and may
	 * subsequently be initialized by a call to {@link #initCause}.
	 *
	 * @param message
	 * 		the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
	 */
	public LifecycleException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new runtime exception with the specified detail message and cause.  <p>Note that the detail
	 * message
	 * associated with <code>cause</code> is <i>not</i> automatically incorporated in this runtime exception's detail
	 * message.
	 *
	 * @param message
	 * 		the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
	 * @param cause
	 * 		the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
	 * 		value is
	 * 		permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public LifecycleException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new runtime exception with the specified cause and a detail message of <tt>(cause==null ? null :
	 * cause.toString())</tt> (which typically contains the class and detail message of <tt>cause</tt>).  This
	 * constructor
	 * is useful for runtime exceptions that are little more than wrappers for other throwables.
	 *
	 * @param cause
	 * 		the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
	 * 		value is
	 * 		permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public LifecycleException(final Throwable cause) {
		super(cause);
	}
}
