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
package io.ceze.regulus.generator.service;

import io.ceze.regulus.account.service.AccountService;
import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.control.service.CollectionService;
import io.ceze.regulus.generator.model.Disposal;
import io.ceze.regulus.generator.model.DisposalInfo;
import io.ceze.regulus.generator.repository.DisposalRepository;
import io.ceze.regulus.generator.web.DisposalRequest;
import io.ceze.regulus.security.User;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class DisposalServiceImpl implements DisposalService {

  private static final Logger LOG = LoggerFactory.getLogger(DisposalService.class);

  @Inject private SecurityContext securityContext;

  @Inject private AccountService accountService;

  @Inject private CollectionService collectionService;

  @Inject private DisposalRepository disposalRepository;

  private User user;

  @PostConstruct
  void initializeUserInfo() {
    String name = securityContext.getCallerPrincipal().getName();
    this.user = accountService.loadByUsername(name);
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
   * @throws RuntimeException if a disposal request has already been initiated at the user's
   *     location
   * @throws NullPointerException if the user associated with the current security context is not
   *     found or if the user's location is null
   */
  @Override
  public DisposalResponse newDisposalRequest(DisposalRequest request) {

    Location location = user.getLocation();

    if (location == null) {
      LOG.error("No location found for the user {}", user.getUsername());
      throw new LocationNotFoundException("Location is not available");
    }

    if (disposalRepository.findByLocationId(location.getId()) != null) {
      LOG.warn("Cannot make multiple request at the same location");
      throw new RuntimeException("Disposal request already initiated at this location");
    }

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
    collectionService.handleDisposal(disposal);
    LOG.info("Processing disposal request {}", disposal.getId());
    return from(disposal);
  }

  public DisposalStatus getDisposalStatus(Long disposalId) {
    LOG.info(
        "User: {} request disposal status for disposal with id {}", user.getUsername(), disposalId);
    Optional<Disposal> disposal = disposalRepository.findById(disposalId);

    return disposal.map(Disposal::getStatus).orElse(null);
  }

  static DisposalResponse from(Disposal disposal) {
    return new DisposalResponse(disposal.getId(), disposal.getStatus());
  }
}
