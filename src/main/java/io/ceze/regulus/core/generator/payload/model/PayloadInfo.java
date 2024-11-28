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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Embeddable
public class PayloadInfo
{

	private int weight;

	@Enumerated(EnumType.STRING)
	@JdbcType(PostgreSQLEnumJdbcType.class)
	@Column(columnDefinition = "priority", name = "pty")
	private Priority priority;

	public PayloadInfo()
	{
	}

	public int getWeight()
	{
		return weight;
	}

	public Priority getPriority()
	{
		return priority;
	}

	public static class Builder
	{
		private int weight;
		private Priority priority;

		public Builder weight(int weight)
		{
			this.weight = weight;
			return this;
		}

		public Builder priority(Priority priority)
		{
			this.priority = priority;
			return this;
		}

		public PayloadInfo build()
		{
			PayloadInfo payloadInfo = new PayloadInfo();
			payloadInfo.weight = this.weight;
			payloadInfo.priority = this.priority;
			return payloadInfo;
		}
	}
}
