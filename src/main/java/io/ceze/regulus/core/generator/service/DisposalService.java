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
package io.ceze.regulus.core.generator.service;

import io.ceze.regulus.core.generator.DuplicateRequestException;
import io.ceze.regulus.core.generator.model.Disposal;
import io.ceze.regulus.core.generator.model.DisposalInfo;
import io.ceze.regulus.core.generator.repository.DisposalRepository;
import io.ceze.regulus.core.generator.web.DisposalRequest;
import io.ceze.regulus.user.domain.model.Location;
import io.ceze.regulus.user.domain.model.Profile;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.service.ProfileService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class DisposalService {

    private static final Logger LOG = LoggerFactory.getLogger(DisposalService.class);

    private final ProfileService profileService;
    private final DisposalRepository disposalRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DisposalService(
            ProfileService profileService,
            DisposalRepository disposalRepository,
            ApplicationEventPublisher eventPublisher) {
        this.profileService = profileService;
        this.disposalRepository = disposalRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Initiates a new disposal request based on the provided {@code DisposalRequest}. This method
     * creates a new disposal request for the user associated with the current security context. It
     * checks if a disposal request has already been initiated at the user's location. If a disposal
     * request already exists, a {@code RuntimeException} is thrown indicating the conflict.
     * Otherwise, a new disposal request is created.
     *
     * @param request the {@code DisposalRequest} containing details of the disposal request
     * @return a {@code DisposalResponse} indicating the status of the new disposal request
     * @throws RuntimeException     if a disposal request has already been initiated at the user's
     *                              location
     * @throws NullPointerException if the user associated with the current security context is not
     *                              found or if the user's location is null
     */
    public DisposalResponse newDisposalRequest(UserId userId, DisposalRequest request)
            throws DuplicateRequestException {

        Profile profile = profileService.getProfileByUserId(userId.id());
        var location = profile.getLocation();

        if (location == null) {
            LOG.error("No location found for the user {}", userId.id());
            throw new LocationNotFoundException("Location is not available");
        }

        checkForPendingRequests(location);

        Disposal disposal =
                new Disposal(
                        request.label(),
                        DisposalStatus.PENDING,
                        new DisposalInfo.Builder()
                                .weight(request.weight())
                                .priority(request.priority())
                                .build());
        disposal.setLocation(location);
        disposal = disposalRepository.save(disposal);
        eventPublisher.publishEvent(disposal);
        LOG.info("Processing disposal request {}", disposal.getId());
        return DisposalResponse.from(disposal);
    }

    @Cacheable
    public Disposal getDisposalById(DisposalId disposalId) throws DisposalNotFoundException {
        var disposal =
                disposalRepository
                        .findByDisposalId(disposalId)
                        .orElseThrow(DisposalNotFoundException::new);

        return disposal;
    }

    public void cancelDisposalRequest(DisposalId disposalId) {
        Disposal disposal = getDisposalById(disposalId);
        if (disposal.getStatus().equals(DisposalStatus.PENDING)) {
            disposal.setStatus(DisposalStatus.CANCELLED);
            LOG.info("Cancelled disposal request with id {}", disposal.getId());
            eventPublisher.publishEvent(new CancelledDisposalEvent(disposal));
        }
    }

    public void updateDisposal(DisposalId disposalId, DisposalRequest request) {
        LOG.info("Updating disposal with id {}", disposalId);
        Disposal disposal = getDisposalById(disposalId);
        if (!disposal.getStatus().equals(DisposalStatus.PENDING)) return;
        disposal.setLabel(request.label());
        disposal.setDisposalInfo(
                new DisposalInfo.Builder()
                        .weight(request.weight())
                        .priority(request.priority())
                        .build());
    }

    @PostConstruct
    public void loadPendingRequests() {
        Disposal probe = new Disposal();
        probe.setStatus(DisposalStatus.PENDING);
        List<Disposal> disposals = disposalRepository.findAll(Example.of(probe));
        LOG.info("Found {} pending requests. Adding to cluster.", disposals.size());
        for (Disposal disposal : disposals) {
            eventPublisher.publishEvent(disposal);
        }
    }

    private void checkForPendingRequests(Location location) {
        Optional<Disposal> optional = disposalRepository.findByLocationId(location.getId());
        optional.ifPresentOrElse(
                e -> {
                    if (e.getStatus().equals(DisposalStatus.PENDING)) {
                        LOG.warn("Cannot make multiple request at the same location");
                        throw new DuplicateRequestException(
                                "Disposal request already initiated at this location");
                    }
                },
                () -> LOG.info("New Disposal request from location {}", location));
    }
}
