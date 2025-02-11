/*
 * Copyright (C) 2024 Emmanuel Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.ceze.regulus.core.collector.model;

import io.ceze.gis.Route;
import jakarta.validation.constraints.NotEmpty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a collection of {@link CollectorAgent}s responsible for carrying out specific tasks
 * in a waste management system or similar operational domain.
 * This class serves as an immutable group of agents, providing utility methods
 * to create and manage the collection, as well as dispatching routes for operational tasks.
 *
 * <p>Use this class to:
 * <ul>
 *   <li>Create a group of collector agents from a single agent or a collection of agents.</li>
 *   <li>Dispatch routes to all agents in the collection to carry out assigned tasks.</li>
 * </ul>
 *
 * <p>This class ensures immutability by copying the provided collection during instantiation,
 * preventing external modifications to the set of agents after creation.
 *
 * @see CollectorAgent
 */
public class CollectorAgents
{

	private final Set<CollectorAgent> agents = new HashSet<>();

	private CollectorAgents(@NotEmpty Set<CollectorAgent> agents)
	{
		this.agents.addAll(agents);
	}

	public static CollectorAgents withAgents(Collection<? extends CollectorAgent> agents)
	{
		return new CollectorAgents(Set.copyOf(agents));
	}

	public static CollectorAgents withAgent(CollectorAgent agent)
	{
		return new CollectorAgents(Set.of(agent));
	}

	/**
	 * Dispatches the specified route to all collector agents in the collection.
	 *
	 * <p>This method is intended to coordinate the assigned task by instructing all
	 * agents to follow the given {@link Route}. The behavior of individual agents
	 * upon dispatch is determined by their implementation.
	 *
	 * @param route the {@link Route} to be dispatched to the collector agents.
	 */
	public void dispatch(Route route)
	{
	}
}
