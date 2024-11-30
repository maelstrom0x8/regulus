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
 *
 */

package io.ceze.regulus.core.processing.model;

import io.ceze.regulus.user.domain.model.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "operators")
public class Operator implements Operable {

	@Id
	@Column(name = "operator_id", columnDefinition = "serial")
	@SequenceGenerator(name = "operator_seq", sequenceName = "operators_id_seq", allocationSize = 1, initialValue = 1000)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operator_seq")
	private Integer id;

	@Column(length = 100, nullable = false, unique = true)
	private String name;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
		name = "operator_payload_types",
		joinColumns = @JoinColumn(name = "operator_id")
	)
	@Enumerated(EnumType.STRING)
	@Column(name = "p_type")
	private final Set<PayloadType> payloadTypes = new HashSet<>();

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private Integer capacity;

	public Operator () {}

	public Operator (String name, User user, Integer capacity)
	{
		this.name = name;
		this.user = user;
		this.capacity = capacity;
	}

	public Integer getId ()
	{
		return id;
	}

	public void setId (Integer id)
	{
		this.id = id;
	}

	public void setCapacity (Integer capacity)
	{
		this.capacity  =  capacity;
	}

	public Integer getCapacity ()
	{
		return capacity;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (String name)
	{
		this.name = name;
	}

	public Set<PayloadType> getPayloadTypes ()
	{
		return payloadTypes;
	}

	public User getUser ()
	{
		return user;
	}

	public void setUser (User user)
	{
		this.user = user;
	}

}

