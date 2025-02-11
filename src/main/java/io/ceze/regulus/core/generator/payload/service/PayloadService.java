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

import io.ceze.gis.LocationNotFoundException;
import io.ceze.regulus.core.generator.payload.model.Payload;
import io.ceze.regulus.core.generator.payload.model.PayloadInfo;
import io.ceze.regulus.core.generator.payload.model.PayloadStatus;
import io.ceze.regulus.core.generator.payload.repository.PayloadRepository;
import io.ceze.regulus.event.CancelledPayloadEvent;
import io.ceze.regulus.event.NewPayloadEvent;
import io.ceze.regulus.user.domain.model.Location;
import io.ceze.regulus.user.domain.model.Profile;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.service.ProfileService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PayloadService
{

	private static final Logger LOG = LoggerFactory.getLogger(PayloadService.class);

	private final ProfileService profileService;
	private final PayloadRepository payloadRepository;
	private final ApplicationEventPublisher eventPublisher;

	public PayloadService(
		ProfileService profileService,
		PayloadRepository payloadRepository,
		ApplicationEventPublisher eventPublisher)
	{
		this.profileService = profileService;
		this.payloadRepository = payloadRepository;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Initiates a new payload request based on the provided {@code PayloadRequest}. This method
	 * creates a new payload request for the user associated with the current security context. It
	 * checks if a payload request has already been initiated at the user's location. If a payload
	 * request already exists, a {@code RuntimeException} is thrown indicating the conflict.
	 * Otherwise, a new payload request is created.
	 *
	 * @param request the {@code PayloadRequest} containing details of the payload request
	 * @return a {@code PayloadResponse} indicating the status of the new payload request
	 * @throws RuntimeException     if a payload request has already been initiated at the user's
	 *                              location
	 * @throws NullPointerException if the user associated with the current security context is not
	 *                              found or if the user's location is null
	 */
	public PayloadResponse initiatePayloadRequest (UserId userId, PayloadRequest request)
		throws DuplicateRequestException
	{

		Profile profile = profileService.getProfileByUserId(userId.id());
		var location = profile.getLocation();

		if (location == null)
		{
			LOG.error("No location found for the user {}", userId.id());
			throw new LocationNotFoundException("Location is not available");
		}

		checkForPendingRequests(location);
		LOG.info("Generating payload for user with id {} at location {}", userId.id(), location.getId());
		Payload payload =
			new Payload(
				request.label(),
				PayloadStatus.PENDING,
				new PayloadInfo.Builder()
					.weight(request.weight())
					.priority(request.priority())
					.build());
		payload.setLocation(location);
		payload = payloadRepository.save(payload);
		eventPublisher.publishEvent(new NewPayloadEvent(payload));
		LOG.info("Processing payload request {}", payload.getId());
		return PayloadResponse.from(payload);
	}

	@Cacheable
	public Payload getPayloadById (PayloadId payloadId) throws PayloadNotFoundException
	{
		var payload =
			payloadRepository
				.findByPayloadId(payloadId.id())
				.orElseThrow(PayloadNotFoundException::new);

		return payload;
	}

	public void cancelPayloadRequest (PayloadId payloadId)
	{
		Payload payload = getPayloadById(payloadId);
		LOG.info("Found payload with id {}, status={}", payloadId.id(), payload.getStatus());
		if (payload.getStatus().equals(PayloadStatus.PENDING))
		{
			payload.setStatus(PayloadStatus.CANCELLED);
			LOG.info("Cancelled payload request with id {}", payload.getId());
			payloadRepository.delete(payload);
			eventPublisher.publishEvent(new CancelledPayloadEvent(payload));
		}
	}

	public void updatePayload(PayloadId payloadId, PayloadRequest request)
	{
		LOG.info("Updating payload with id {}", payloadId);
		Payload payload = getPayloadById(payloadId);
		if (!payload.getStatus().equals(PayloadStatus.PENDING)) return;
		payload.setLabel(request.label());
		payload.setPayloadInfo(
			new PayloadInfo.Builder()
				.weight(request.weight())
				.priority(request.priority())
				.build());

		LOG.info("Updated payload with id {}", payload.getId());
	}

	@PostConstruct
	public void loadPendingRequests()
	{
		Payload probe = new Payload();
		probe.setStatus(PayloadStatus.PENDING);
		List<Payload> payloads = payloadRepository.findAll(Example.of(probe));
		LOG.info("Found {} pending requests.{}", payloads.size(), !payloads.isEmpty() ? " Adding to cluster." : "");
		for (Payload payload : payloads)
		{
			eventPublisher.publishEvent(payload);
		}
	}

	private void checkForPendingRequests(Location location)
	{
		Optional<Payload> optional = payloadRepository.findByLocationId(location.getId());
		optional.ifPresentOrElse(
			e ->
			{
				if (e.getStatus().equals(PayloadStatus.PENDING))
				{
					LOG.warn("Cannot make multiple request at the same location");
					throw new DuplicateRequestException(
						"Payload request already initiated at this location");
				}
			},
			() -> LOG.info("New Payload request from location {}", location));
	}
}
