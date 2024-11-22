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
package io.ceze.regulus.core.generator.web.controller;

import io.ceze.config.security.Authenticated;
import io.ceze.regulus.core.generator.DuplicateRequestException;
import io.ceze.regulus.core.generator.model.Disposal;
import io.ceze.regulus.core.generator.service.DisposalId;
import io.ceze.regulus.core.generator.service.DisposalResponse;
import io.ceze.regulus.core.generator.service.DisposalService;
import io.ceze.regulus.core.generator.web.DisposalRequest;
import io.ceze.regulus.user.domain.model.projection.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/disposals")
public class DisposalController {

    private static final Logger LOG = LoggerFactory.getLogger(DisposalController.class);
    private final DisposalService disposalService;

    public DisposalController(DisposalService disposalService) {
        this.disposalService = disposalService;
    }

    @PostMapping
    public ResponseEntity<?> requestDisposal(
            @Authenticated UserId userId, @NotNull @Valid @RequestBody DisposalRequest request) {

        LOG.info("Initiating new disposal request: {}", request);
        DisposalResponse response;
        try {
            response = disposalService.newDisposalRequest(userId, request);
        } catch (DuplicateRequestException e) {
            return ResponseEntity.badRequest()
                    .body("Cannot initiate multiple request for the same location");
        }
        LOG.info("Disposal request successfully initiated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancel/{id}")
    public void cancelDisposal(@Authenticated UserId userId, @PathVariable("id") Long disposalId) {
        DisposalId id = new DisposalId(userId.id(), disposalId);
        disposalService.cancelDisposalRequest(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisposalResponse> fetchDisposalById(
            @Authenticated UserId userId, @PathVariable("id") Long disposalId) {
        Disposal disposal =
                disposalService.getDisposalById(new DisposalId(userId.id(), disposalId));
        return ResponseEntity.ok(DisposalResponse.from(disposal));
    }
}
