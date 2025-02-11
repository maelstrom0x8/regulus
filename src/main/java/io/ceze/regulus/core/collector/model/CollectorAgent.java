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

import io.ceze.regulus.user.domain.model.Location;
import jakarta.persistence.*;

@Entity
@Table(name = "agents")
public class CollectorAgent
{

	@Id
	@Column(name = "agent_id")
	@SequenceGenerator(
		name = "agent_seq",
		sequenceName = "agents_id_seq",
		initialValue = 1000,
		allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "agent_seq")
	private Long id;

	private boolean available;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "collector_id")
	private Collector collector;

	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location location;

	public CollectorAgent()
	{
	}

	public CollectorAgent(Collector collector)
	{
		this.collector = collector;
	}

	public Long getId()
	{
		return id;
	}

	public boolean isAvailable()
	{
		return available;
	}

	public void setAvailable(boolean available)
	{
		this.available = available;
	}

	public Collector getCollector()
	{
		return collector;
	}

	public void setCollector(Collector collector)
	{
		this.collector = collector;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}
}
