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

import io.ceze.regulus.control.service.CollectionService;
import io.ceze.regulus.generator.DisposalRepository;
import io.ceze.regulus.generator.model.Disposal;
import io.ceze.regulus.generator.model.DisposalInfo;
import io.ceze.regulus.generator.web.DisposalRequest;
import io.ceze.regulus.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;

@ApplicationScoped
public class DefaultWasteService implements WasteService {

  @Inject private SecurityContext securityContext;

  @Inject private UserService userService;

  @Inject private CollectionService collectionService;

  @Inject private DisposalRepository disposalRepository;

  @Override
  public DisposalResponse newDisposalRequest(DisposalRequest request) {
    String username = securityContext.getCallerPrincipal().getName();
    var user = userService.loadByUsername(username);

    Disposal disposal =
        new Disposal(
            request.label(),
            request.status(),
            new DisposalInfo.Builder()
                .weight(request.weight())
                .priority(request.priority())
                .build());
    disposal.setLocation(user.getLocation());
    disposalRepository.save(disposal);
    return collectionService.handleDisposal(disposal);
  }

  public DisposalResponse getDisposalStatus(Long disposalId) {
    Disposal disposal = disposalRepository.findById(disposalId).orElseThrow();
    return from(disposal);
  }

  static DisposalResponse from(Disposal disposal) {
    return new DisposalResponse(disposal.getId(), disposal.getStatus());
  }
}
