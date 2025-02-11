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

import io.ceze.regulus.user.domain.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "collectors")
public class Collector extends User
{

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "collector")
	private Set<CollectorAgent> collectorAgents;

	public Collector() {}

	public Set<CollectorAgent> getCollectorAgents()
	{
		return collectorAgents;
	}

	public void addAgents(@NotNull Collection<CollectorAgent> agents)
	{
		this.collectorAgents.addAll(agents);
	}

	public void addAgent(@NotNull CollectorAgent agent)
	{
		collectorAgents.add(agent);
	}

	public void removeAgent(@NotNull AgentId agentId)
	{
		collectorAgents.removeIf(agent ->
		{
			if (agentId.collectorId().equals(id))
			{
				return collectorAgents.stream().anyMatch(e -> e.getId().equals(agentId.agentId()));
			}
			return false;
		});
	}
}
