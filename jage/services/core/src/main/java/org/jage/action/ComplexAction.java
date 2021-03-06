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
 * Created: 2008-10-07
 * $Id: ComplexAction.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.action;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Container for actions which allows to create tree structure of actions.
 *
 * @author AGH AgE Team
 *
 */
public class ComplexAction extends Action {

	/**
	 * Children list.
	 */
	private final List<Action> children = newLinkedList();

	/**
	 * Adds a child action to this complex action.
	 *
	 * @param action
	 *            the new child action.
	 */
	public void addChild(final Action action) {
		children.add(action);
	}

	/**
	 * Returns all children actions.
	 *
	 * @return all children actions.
	 */
	public List<Action> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("children", children).toString();
	}
}
