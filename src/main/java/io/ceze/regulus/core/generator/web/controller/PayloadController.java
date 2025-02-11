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
package io.ceze.regulus.core.generator.web.controller;

import io.ceze.config.security.Authenticated;
import io.ceze.regulus.core.generator.payload.model.Payload;
import io.ceze.regulus.core.generator.payload.service.DuplicateRequestException;
import io.ceze.regulus.core.generator.payload.service.PayloadId;
import io.ceze.regulus.core.generator.payload.service.PayloadRequest;
import io.ceze.regulus.core.generator.payload.service.PayloadResponse;
import io.ceze.regulus.core.generator.payload.service.PayloadService;
import io.ceze.regulus.user.domain.model.projection.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('GENERATOR')")
@RequestMapping("/v1/payloads")
@RestController
public class PayloadController
{

	private static final Logger LOG = LoggerFactory.getLogger(PayloadController.class);
	private final PayloadService payloadService;

	public PayloadController(PayloadService payloadService)
	{
		this.payloadService = payloadService;
	}

	@PostMapping
	public ResponseEntity<?> createPayloadRequest (
		@Authenticated UserId userId, @NotNull @Valid @RequestBody PayloadRequest request)
	{

		LOG.info("Initiating new payload request: {}", request);
		PayloadResponse response;
		try
		{
			response = payloadService.initiatePayloadRequest(userId, request);
		} catch (DuplicateRequestException e)
		{
			return ResponseEntity.badRequest()
				.body("Cannot initiate multiple request for the same location");
		}
		LOG.info("Payload request successfully initiated");
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/cancel/{id}")
	public void cancelPayloadRequest(@Authenticated UserId userId, @PathVariable("id") Long payloadId)
	{
		LOG.info("Process payload cancellation");
		PayloadId id = new PayloadId(userId.id(), payloadId);
		payloadService.cancelPayloadRequest(id);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PayloadResponse> fetchPayloadById(
		@Authenticated UserId userId, @PathVariable("id") Long disposalId)
	{
		Payload payload =
			payloadService.getPayloadById(new PayloadId(userId.id(), disposalId));
		return ResponseEntity.ok(PayloadResponse.from(payload));
	}

	@PutMapping("/{id}")
	public void updatePayload (@Authenticated UserId userId, @PathVariable("id") Long id,
		@RequestBody PayloadRequest request)
	{
		PayloadId payloadId = new PayloadId(userId.id(), id);
		payloadService.updatePayload(payloadId, request);
	}
}
