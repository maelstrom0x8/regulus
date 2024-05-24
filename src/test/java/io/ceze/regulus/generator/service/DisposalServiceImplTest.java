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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.ceze.regulus.account.service.AccountService;
import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.commons.data.LocationRepository;
import io.ceze.regulus.core.control.service.CollectionService;
import io.ceze.regulus.generator.DuplicateRequestException;
import io.ceze.regulus.generator.model.Disposal;
import io.ceze.regulus.generator.model.Label;
import io.ceze.regulus.generator.repository.DisposalRepository;
import io.ceze.regulus.generator.web.DisposalRequest;
import io.ceze.regulus.generator.web.Priority;
import io.ceze.regulus.security.User;
import jakarta.security.enterprise.SecurityContext;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DisposalServiceImplTest {

    @InjectMocks DisposalServiceImpl disposalService = new DisposalServiceImpl();

    @Mock private SecurityContext securityContext;
    @Mock private AccountService accountService;
    @Mock private CollectionService collectionService;
    @Mock private DisposalRepository disposalRepository;
    @Mock private LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void userWithNullLocationCannotInitiateRequest() {
        when(securityContext.getCallerPrincipal()).thenReturn(() -> "bob");
        User bob = new User("bob", "pass", null);
        when(accountService.loadByUsername("bob")).thenReturn(bob);
        disposalService.initializeUserInfo();
        DisposalRequest request = new DisposalRequest(Label.MSW, 10, Priority.MEDIUM);

        assertNull(bob.getLocation());
        assertThrows(
                LocationNotFoundException.class,
                () -> {
                    disposalService.newDisposalRequest(request);
                });
    }

    @Test
    void shouldCreateRequestForNewLocation() {

        Location location = new Location();
        location.setId(1L);
        User bob = new User("bob", "pass", null);
        bob.setLocation(location);
        Disposal disposal = new Disposal();
        disposal.setId(123L);
        disposal.setStatus(DisposalStatus.PENDING);
        when(disposalRepository.save(any(Disposal.class)))
                .thenAnswer(
                        invocation -> {
                            Disposal savedDisposal = invocation.getArgument(0);
                            savedDisposal.setId(disposal.getId());
                            return savedDisposal;
                        });

        when(securityContext.getCallerPrincipal()).thenReturn(() -> "bob");
        when(accountService.loadByUsername("bob")).thenReturn(bob);
        disposalService.initializeUserInfo();
        when(disposalRepository.findByLocationId(location.getId())).thenReturn(Optional.empty());
        DisposalRequest request = new DisposalRequest(Label.MSW, 10, Priority.MEDIUM);
        DisposalResponse response = disposalService.newDisposalRequest(request);

        verify(disposalRepository, times(1)).save(any(Disposal.class));
        verify(collectionService, times(1)).handleDisposal(any(Disposal.class));

        assertNotNull(response);
    }

    @Test
    void userCannotInitiateMultipleRequests() {
        Location location = new Location();
        location.setId(1L);
        User bob = new User("bob", "pass", null);
        bob.setLocation(location);
        Disposal disposal = new Disposal();
        disposal.setId(123L);
        disposal.setStatus(DisposalStatus.PENDING);

        when(disposalRepository.save(any(Disposal.class)))
                .thenAnswer(
                        invocation -> {
                            Disposal savedDisposal = invocation.getArgument(0);
                            savedDisposal.setId(disposal.getId());
                            return savedDisposal;
                        });

        when(securityContext.getCallerPrincipal()).thenReturn(() -> "bob");
        when(accountService.loadByUsername("bob")).thenReturn(bob);
        disposalService.initializeUserInfo();
        DisposalRequest request = new DisposalRequest(Label.MSW, 10, Priority.MEDIUM);

        disposalService.newDisposalRequest(request);
        when(disposalRepository.findByLocationId(any(Long.class)))
                .thenReturn(Optional.of(disposal));

        assertThrows(
                DuplicateRequestException.class,
                () -> {
                    disposalService.newDisposalRequest(request);
                });
    }
}
