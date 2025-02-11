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
package io.ceze.regulus.core.generator.payload.model;

import io.ceze.regulus.user.domain.model.Location;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Table(name = "payloads")
public class Payload
{

	@Id
	@Column(name = "payload_id", columnDefinition = "bigserial")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(name = "lbl", columnDefinition = "label")
	private Label label;

	@Column(name = "status", columnDefinition = "payload_status")
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Enumerated(EnumType.STRING)
	private PayloadStatus status;

	private PayloadInfo payloadInfo;

	@CreationTimestamp
	private LocalDateTime initiatedAt;

	@UpdateTimestamp
	private LocalDateTime lastModified;

	@ManyToOne
	@JoinColumn(name = "location_id")
	private Location location;

	public Payload()
	{
	}

	public Payload(Label label, PayloadStatus status, PayloadInfo payloadInfo)
	{
		this.label = label;
		this.status = status;
		this.payloadInfo = payloadInfo;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(long l)
	{
		this.id = l;
	}

	public Label getLabel()
	{
		return label;
	}

	public void setLabel(Label label)
	{
		this.label = label;
	}

	public PayloadStatus getStatus()
	{
		return status;
	}

	public void setStatus(PayloadStatus status)
	{
		this.status = status;
	}

	public PayloadInfo getPayloadInfo ()
	{
		return payloadInfo;
	}

	public void setPayloadInfo (PayloadInfo payloadInfo)
	{
		this.payloadInfo = payloadInfo;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}

	public LocalDateTime getLastModified()
	{
		return lastModified;
	}

	public LocalDateTime getInitiatedAt()
	{
		return initiatedAt;
	}
}
