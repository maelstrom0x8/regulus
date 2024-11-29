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
package io.ceze.regulus.core.generator.payload.service;

import io.ceze.regulus.core.generator.payload.model.Label;
import io.ceze.regulus.core.generator.payload.model.Payload;
import io.ceze.regulus.core.generator.payload.model.PayloadInfo;
import io.ceze.regulus.core.generator.payload.model.PayloadStatus;

import java.time.LocalDateTime;

public record PayloadResponse(
	Long id,
	Label label,
	PayloadStatus status,
	PayloadInfo payloadInfo,
	LocalDateTime created,
	LocalDateTime lastModified,
	Long origin)
{
	public static PayloadResponse from(Payload payload)
	{
		return new PayloadResponse(
			payload.getId(),
			payload.getLabel(),
			payload.getStatus(),
			payload.getPayloadInfo(),
			payload.getInitiatedAt(),
			payload.getLastModified(),
			payload.getLocation().getId());
	}
}
