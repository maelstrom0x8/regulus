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
package io.ceze.regulus.core.payload.service;

import io.ceze.regulus.core.collector.service.CollectorService;
import io.ceze.regulus.core.generator.payload.repository.PayloadRepository;
import io.ceze.regulus.core.generator.payload.service.PayloadService;
import io.ceze.regulus.user.domain.repository.LocationRepository;
import io.ceze.regulus.user.domain.service.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class PayloadServiceImplTest
{

	@InjectMocks
	PayloadService payloadService;

	@Mock
	private Authentication securityContext;
	@Mock
	private UserService userService;
	@Mock
	private CollectorService collectorService;
	@Mock
	private PayloadRepository payloadRepository;
	@Mock
	private LocationRepository locationRepository;

    /*@Test
    void userWithNullLocationCannotInitiateRequest() {
        when(securityContext.getName()).thenReturn("bob");
        User bob = new User("bob@mail.com");
        when(userService.getUserByEmail("bob")).thenReturn(bob);
        payloadService.initializeUserInfo();
        PayloadRequest request = new PayloadRequest(Label.MSW, 10, Priority.MEDIUM);

        assertNull(bob.getLocation());
        assertThrows(
                LocationNotFoundException.class,
                () -> {
                    payloadService.newDisposalRequest(request);
                });
    }

    @Test
    void shouldCreateRequestForNewLocation() {

        Location location = new Location();
        location.setId(1L);
        User bob = new User("bob", "pass", null);
        bob.setLocation(location);
        Payload payload = new Payload();
        payload.setId(123L);
        payload.setStatus(PayloadStatus.PENDING);
        when(payloadRepository.save(any(Payload.class)))
                .thenAnswer(
                        invocation -> {
                            Payload savedDisposal = invocation.getArgument(0);
                            savedDisposal.setId(payload.getId());
                            return savedDisposal;
                        });

        when(securityContext.getName()).thenReturn("bob");
        when(accountService.loadByUsername("bob")).thenReturn(bob);
        payloadService.initializeUserInfo();
        when(payloadRepository.findByLocationId(location.getId())).thenReturn(Optional.empty());
        PayloadRequest request = new PayloadRequest(Label.MSW, 10, Priority.MEDIUM);
        PayloadResponse response = payloadService.newDisposalRequest(request);

        verify(payloadRepository, times(1)).save(any(Payload.class));
        verify(collectorService, times(1)).handleDisposal(any(Payload.class));

        assertNotNull(response);
    }

    @Test
    void userCannotInitiateMultipleRequests() {
        Location location = new Location();
        location.setId(1L);
        User bob = new User("bob", "pass", null);
        bob.setLocation(location);
        Payload payload = new Payload();
        payload.setId(123L);
        payload.setStatus(PayloadStatus.PENDING);

        when(payloadRepository.save(any(Payload.class)))
                .thenAnswer(
                        invocation -> {
                            Payload savedDisposal = invocation.getArgument(0);
                            savedDisposal.setId(payload.getId());
                            return savedDisposal;
                        });

        when(securityContext.getName()).thenReturn("bob");
        when(accountService.loadByUsername("bob")).thenReturn(bob);
        payloadService.initializeUserInfo();
        PayloadRequest request = new PayloadRequest(Label.MSW, 10, Priority.MEDIUM);

        payloadService.newDisposalRequest(request);
        when(payloadRepository.findByLocationId(any(Long.class)))
                .thenReturn(Optional.of(payload));

        assertThrows(
                DuplicateRequestException.class,
                () -> {
                    payloadService.newDisposalRequest(request);
                });
    }*/
}
