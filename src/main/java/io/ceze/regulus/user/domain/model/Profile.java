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
package io.ceze.regulus.user.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "profiles")
public class Profile
{

	@Id
	@Column(name = "profile_id")
	@SequenceGenerator(
		name = "profile_seq",
		sequenceName = "profiles_id_seq",
		allocationSize = 1,
		initialValue = 1000)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_seq")
	private Long id;

	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
	@JoinColumn(name = "location_id")
	private Location location;

	@ElementCollection
	@CollectionTable(name = "profile_attributes", joinColumns = @JoinColumn(name = "profile_id"))
	@MapKeyColumn(name = "attr_key")
	@Column(name = "attr_value")
	private final Map<String, String> attributes = new HashMap<>();

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime lastModified;

	@Column(name = "full_name", length = 100)
	private String name;

	public Profile() {}

	public Profile(User user)
	{
		this.user = user;
	}

	public Long getId()
	{
		return id;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}

	public LocalDateTime getLastModified()
	{
		return lastModified;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}

	public Map<String, String> getAttributes()
	{
		return attributes;
	}

	public String getAttribute(String key)
	{
		return attributes.values()
			.stream().filter(e -> e.equals(key)).findFirst().orElse(null);
	}

	public void addAttribute(String name, String value)
	{
		attributes.put(name, value);
	}

	public void addAttributes(Map<String, String> attrs)
	{
		attributes.putAll(attrs);
	}
}
