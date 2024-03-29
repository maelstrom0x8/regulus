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
package io.ceze.regulus.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.ceze.regulus.account.repository.AccountRepository;
import io.ceze.regulus.account.web.LocationData;
import io.ceze.regulus.account.web.ProfileDataRequest;
import io.ceze.regulus.account.web.ProfileDataResponse;
import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.commons.data.LocationRepository;
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
class ProfileServiceTest {

    @InjectMocks ProfileService profileService;

    @Mock SecurityContext securityContext;

    @Mock AccountRepository accountRepository;

    @Mock LocationRepository locationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updatingProfileUpdatesLocation() {
        when(securityContext.getCallerPrincipal()).thenReturn(() -> "user");

        User user = new User("user", "secret", null);
        Location location = new Location();
        location.setId(434L);
        location.setCity("city");
        location.setState("state");
        location.setCountry("country");
        LocationData locationData = Location.from(location);

        when(accountRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(locationRepository.save(any(Location.class)))
                .thenAnswer(
                        inv -> {
                            Location loc = inv.getArgument(0);
                            loc.setId(location.getId());
                            return loc;
                        });

        profileService.initializeUser();

        ProfileDataRequest request = new ProfileDataRequest("user", "newEmail", locationData);
        ProfileDataResponse response = profileService.updateProfile("user", request);

        assertEquals("user", user.getUsername());
        assertEquals("newEmail", user.getEmail());
        assertEquals("country", user.getLocation().getCountry());
        assertEquals("state", user.getLocation().getState());
        assertEquals("city", user.getLocation().getCity());

        assertEquals("user", response.username());
        assertEquals("country", response.location().country());
        assertEquals("state", response.location().state());
        assertEquals("city", response.location().city());
    }
}
