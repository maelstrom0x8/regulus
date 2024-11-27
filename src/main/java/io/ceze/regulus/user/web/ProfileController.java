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
package io.ceze.regulus.user.web;

import io.ceze.config.security.Authenticated;
import io.ceze.regulus.user.domain.model.Profile;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.service.ProfileService;
import io.ceze.regulus.user.dto.ProfileRequest;
import io.ceze.regulus.user.dto.ProfileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/profiles")
public class ProfileController
{

	private static final Logger log = LoggerFactory.getLogger(ProfileController.class);
	private final ProfileService profileService;

	public ProfileController(ProfileService profileService)
	{
		this.profileService = profileService;
	}

	@PostMapping
	public void createProfile(@Authenticated UserId userId, @RequestBody ProfileRequest request)
	{
		log.info("Creating profile for user with id {}", userId.id());
		profileService.create(userId, request);
		log.info("Profile created successfully");
	}

	@GetMapping
	public ResponseEntity<ProfileResponse> fetchUserProfile(@Authenticated UserId userId)
	{
		log.info("Fetching profile for user {}", userId.id());
		Profile profile = profileService.getProfileByUserId(userId.id());
		ProfileResponse response = ProfileResponse.from(profile);
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public void updateProfile(@Authenticated UserId userId, @RequestBody ProfileRequest request)
	{
		profileService.updateProfile(userId, request);
	}
}
