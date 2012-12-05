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
 * $Id: AbstractAgent.java 471 2012-10-30 11:17:00Z faber $
 */

package org.jage.agent;

import java.util.Collection;
import java.util.Queue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jage.address.agent.AgentAddress;
import org.jage.communication.message.IMessage;
import org.jage.platform.component.exception.ComponentException;
import org.jage.platform.component.provider.IComponentInstanceProvider;
import org.jage.platform.component.provider.IComponentInstanceProviderAware;
import org.jage.property.ClassPropertyContainer;
import org.jage.property.IPropertyContainer;
import org.jage.property.InvalidPropertyPathException;
import org.jage.property.Property;
import org.jage.property.PropertyGetter;
import org.jage.query.AgentEnvironmentQuery;
import org.jage.workplace.IllegalOperationException;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Queues.newLinkedBlockingQueue;

/**
 * Abstract agent implementation.
 *
 * @author AGH AgE Team
 */
public abstract class AbstractAgent extends ClassPropertyContainer implements IAgent, IComponentInstanceProviderAware {

	private static final long serialVersionUID = 3L;

	/**
	 * AbstractAgent properties.
	 *
	 * @author AGH AgE Team
	 */
	public static class Properties {

		/**
		 * Agent address property.
		 */
		public static final String ADDRESS = "address";
	}

	private final AgentAddress address;

	private final Queue<IMessage<AgentAddress, ?>> messages = newLinkedBlockingQueue();

	protected IComponentInstanceProvider instanceProvider;

    public AbstractAgent(@Nonnull final AgentAddress address) {
    	this.address = checkNotNull(address);
    }

	@Override
	@PropertyGetter(propertyName = Properties.ADDRESS)
	public final @Nullable AgentAddress getAddress() {
		return address;
	}

	@Override
	public void init() throws ComponentException {
	}

	@Override
	public boolean finish() throws ComponentException {
		return true;
	}

	@Override
	public final void deliverMessage(final IMessage<AgentAddress, ?> message) {
		messages.add(message);
	}

	/**
	 * Returns this agent's messages queue.
	 *
     * @return this agent's messages queue
     */
    protected final Queue<IMessage<AgentAddress, ?>> getMessages() {
	    return messages;
    }

	@Override
	public void setInstanceProvider(final IComponentInstanceProvider provider) {
		instanceProvider = provider;
	}

	/**
	 * Provides local environment. An agent don't have to be in an environment, because it is root agent or the
	 * environment is not yet set.
	 *
	 * @return instance of environment or null
	 */
	protected abstract @Nullable IAgentEnvironment getAgentEnvironment();

	/**
	 * Checks if this agent has a local environment.
	 *
	 * @return <TT>true</TT> if this agent has a local environment; otherwise - <TT>false</TT>
	 */
	protected final boolean hasAgentEnvironment() {
		return getAgentEnvironment() != null;
	}

	/**
	 * Queries the local environment of this agent.
	 * <p>
	 * This is just an utility method that basically calls: <code>
	 * query.execute(agentEnvironment)
	 * </code> after verifying parameters.
	 *
	 * @param query
	 *            The query to perform on the agent's local environment.
	 *
	 * @return A result of the query.
	 *
	 * @throws IllegalStateException
	 *             if the environment is not available.
	 */
	protected <E extends IAgent, T> Collection<T> queryEnvironment(final AgentEnvironmentQuery<E, T> query) {
		checkState(hasAgentEnvironment(), "Agent has no environment.");
		return query.execute(getAgentEnvironment());
	}

	/**
	 * Queries the local environment of a parent of this agent.
	 *
	 * @param query
	 *            The query to perform on the agent's parent's local environment.
	 *
	 * @return A result of the query.
	 *
	 * @throws IllegalStateException
	 *             if the environment is not available.
	 *
	 * @see IAgentEnvironment#queryParent(AgentEnvironmentQuery)
	 */
	protected <E extends IAgent, T> Collection<T> queryParentEnvironment(final AgentEnvironmentQuery<E, T> query)
	        throws AgentException {
		checkState(hasAgentEnvironment(), "Agent has no environment.");
		return getAgentEnvironment().queryParent(query);
	}

	/**
	 * Returns the address of the parent of this agent.
	 *
	 * @return the address of the parent of this agent or null if parent is not available.
	 */
	protected @Nullable AgentAddress getParentAddress() {
		if (hasAgentEnvironment()) {
			return getAgentEnvironment().getAddress();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This method is overridden to implement virtual properties. If agent can't find property with given path it ask
	 * about the property it's parent (through agent environment).
	 */
	@Override
	public final @Nullable Property getProperty(final String name) throws InvalidPropertyPathException {
		Property result = null;
		try {
			result = super.getProperty(name);
		} catch (final InvalidPropertyPathException e) {
			try {
				if (getAgentEnvironment() instanceof IPropertyContainer) {
					result = ((IPropertyContainer)getAgentEnvironment()).getProperty(name);
				}
			} catch (final InvalidPropertyPathException e2) {
				throw e;
			} catch (final IllegalOperationException e2) {
				throw e;
			}
		}
		return result;
	}

	@Override
	public final int hashCode() {
		return Objects.hashCode(address);
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractAgent)) {
			return false;
		}
		final AbstractAgent other = (AbstractAgent)obj;
		return Objects.equal(address, other.address);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("address", address).toString();
	}
}
