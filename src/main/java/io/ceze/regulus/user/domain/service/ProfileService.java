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
package io.ceze.regulus.user.domain.service;

import io.ceze.regulus.user.domain.model.Location;
import io.ceze.regulus.user.domain.model.Profile;
import io.ceze.regulus.user.domain.model.User;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.repository.LocationRepository;
import io.ceze.regulus.user.domain.repository.ProfileRepository;
import io.ceze.regulus.user.dto.ProfileRequest;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final UserService userService;
    private final ProfileRepository profileRepository;
    private final LocationRepository locationRepository;

    public ProfileService(
            UserService userService,
            ProfileRepository profileRepository,
            LocationRepository locationRepository) {
        this.userService = userService;
        this.profileRepository = profileRepository;
        this.locationRepository = locationRepository;
    }

    public Profile getProfileById(Long profileId) {
        return profileRepository.findById(profileId).orElseThrow(ProfileNotFoundException::new);
    }

    public Profile getProfileByUserId(Long userId) {
        Profile profile =
                profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);

        return profile;
    }

    public void create(UserId userId, ProfileRequest profileRequest) {
        User user = userService.getUserById(userId.id());
        Profile profile = new Profile(user);
        Location location = new Location();
        profile.setLocation(location);

        profileRequest.emplace(profile);
        locationRepository.save(location);
        profileRepository.save(profile);
    }

    public void updateProfile(UserId userId, ProfileRequest request) {
        Profile profile = getProfileByUserId(userId.id());
        request.emplace(profile);
    }

    public Location getUserLocation(Long userId) {
        Profile profile = profileRepository.findByUserId(userId).orElseThrow();
        return profile.getLocation();
    }
}