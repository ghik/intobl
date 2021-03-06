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
 * Created: 30-03-2012
 * $Id: VerificationTest.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.platform.component.pico;

import javax.inject.Inject;

import org.junit.Test;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.visitors.VerifyingVisitor;

/**
 * Some verification tests.
 *
 * @author AGH AgE Team
 */
public class VerificationTest {

	@Test(expected = PicoVerificationException.class)
	public void shouldDetectCircularMemberDependency() {
		// given
		final PicoComponentInstanceProvider container = new PicoComponentInstanceProvider();
		container.addComponent(MemberA.class);
		container.addComponent(MemberB.class);
		container.addComponent(MemberC.class);

		// when
		new VerifyingVisitor().traverse(container);
	}

	public static class MemberA {
        @Inject
		public MemberB memberB;
	}

	public static class MemberB {
		@Inject
		public MemberC memberC;
	}

	public static class MemberC {
		@Inject
		public MemberA memberA;
	}

	@Test(expected = PicoVerificationException.class)
	public void shouldDetectCircularConstructorDependency() {
		// given
		final PicoComponentInstanceProvider container = new PicoComponentInstanceProvider();
		container.addComponent(ConstructorA.class);
		container.addComponent(ConstructorB.class);
		container.addComponent(ConstructorC.class);

		// when
		new VerifyingVisitor().traverse(container);
	}

	public static class ConstructorA {
		@Inject
        public ConstructorA(final ConstructorB constructorB) {
        }
	}

	public static class ConstructorB {
		@Inject
        public ConstructorB(final ConstructorC constructorC) {
        }
	}

	public static class ConstructorC {
		@Inject
		public ConstructorC(final ConstructorA constructorA) {
        }
	}



}
