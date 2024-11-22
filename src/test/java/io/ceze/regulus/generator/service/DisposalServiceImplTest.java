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

import io.ceze.regulus.core.control.service.CollectionService;
import io.ceze.regulus.core.generator.repository.DisposalRepository;
import io.ceze.regulus.core.generator.service.DisposalService;
import io.ceze.regulus.user.domain.repository.LocationRepository;
import io.ceze.regulus.user.domain.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class DisposalServiceImplTest {

    @InjectMocks DisposalService disposalService;

    @Mock private Authentication securityContext;
    @Mock private UserService userService;
    @Mock private CollectionService collectionService;
    @Mock private DisposalRepository disposalRepository;
    @Mock private LocationRepository locationRepository;

    /*@Test
    void userWithNullLocationCannotInitiateRequest() {
        when(securityContext.getName()).thenReturn("bob");
        User bob = new User("bob@mail.com");
        when(userService.getUserByEmail("bob")).thenReturn(bob);
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

        when(securityContext.getName()).thenReturn("bob");
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

        when(securityContext.getName()).thenReturn("bob");
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
    }*/
}
