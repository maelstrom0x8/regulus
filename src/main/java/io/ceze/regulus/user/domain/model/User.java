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

import io.ceze.regulus.core.collector.model.Collector;
import io.ceze.regulus.core.generator.payload.model.Generator;
import io.ceze.regulus.core.processing.model.Landfill;
import io.ceze.regulus.core.processing.model.Recycler;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User
{

	@Id
	@Column(name = "user_id")
	@SequenceGenerator(
		name = "user_seq",
		sequenceName = "users_id_seq",
		initialValue = 1000,
		allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
	protected Long id;

	@Column(nullable = false, unique = true, length = 254)
	protected String email;

	protected transient Role role;
	protected boolean verified;
	protected boolean active;

	@CreationTimestamp
	protected LocalDateTime createdAt;

	@UpdateTimestamp
	protected LocalDateTime lastModified;

	public User()
	{
	}

	public User(String email)
	{
		this.email = email;
	}

	public static User withRole(Role role)
	{
		if (role == null)
		{
			throw new IllegalArgumentException("Role cannot be null");
		}

		return switch (role)
		{
			case GENERATOR -> new Generator();
			case COLLECTOR -> new Collector();
			case RECYCLER -> new Recycler();
			case LANDFILL_OPERATOR -> new Landfill();
		};
	}

	public Long getId()
	{
		return id;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public boolean getVerified()
	{
		return verified;
	}

	public void setVerified(boolean verified)
	{
		this.verified = verified;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt)
	{
		this.createdAt = createdAt;
	}

	public LocalDateTime getLastModified()
	{
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified)
	{
		this.lastModified = lastModified;
	}

	public Role getRole()
	{
		return determineRole();
	}

	private Role determineRole()
	{
		if (this instanceof Generator)
		{
			return Role.GENERATOR;
		} else if (this instanceof Collector)
		{
			return Role.COLLECTOR;
		} else if (this instanceof Recycler)
		{
			return Role.RECYCLER;
		} else if (this instanceof Landfill)
		{
			return Role.LANDFILL_OPERATOR;
		}else
		{
			throw new IllegalStateException("Unknown role for user");
		}
	}
}
