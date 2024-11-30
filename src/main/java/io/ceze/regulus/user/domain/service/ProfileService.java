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

import io.ceze.regulus.core.processing.model.Landfill;
import io.ceze.regulus.core.processing.model.Recycler;
import io.ceze.regulus.event.OperatorCreated;
import io.ceze.regulus.user.domain.model.Location;
import io.ceze.regulus.user.domain.model.Profile;
import io.ceze.regulus.user.domain.model.User;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.repository.LocationRepository;
import io.ceze.regulus.user.domain.repository.ProfileRepository;
import io.ceze.regulus.user.dto.ProfileRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService
{

	private final ApplicationEventPublisher eventPublisher;
	private final UserService userService;
	private final ProfileRepository profileRepository;
	private final LocationRepository locationRepository;

	public ProfileService(
		ApplicationEventPublisher eventPublisher,
		UserService userService,
		ProfileRepository profileRepository,
		LocationRepository locationRepository)
	{
		this.eventPublisher = eventPublisher;
		this.userService = userService;
		this.profileRepository = profileRepository;
		this.locationRepository = locationRepository;
	}

	public Profile getProfileById(Long profileId)
	{
		return profileRepository.findById(profileId).orElseThrow(ProfileNotFoundException::new);
	}

	public Profile getProfileByUserId(Long userId)
	{
		Profile profile =
			profileRepository.findByUserId(userId).orElseThrow(ProfileNotFoundException::new);

		return profile;
	}

	@Transactional
	public void create (UserId userId, ProfileRequest profileRequest)
	{
		User user = userService.getUserById(userId.id());
		Profile profile = new Profile(user);
		Location location = new Location();
		profile.setLocation(location);

		profileRequest.emplace(profile);
		locationRepository.save(location);
		profileRepository.save(profile);
		eventPublisher.publishEvent(location);
		if (user instanceof Recycler || user instanceof Landfill)
			eventPublisher.publishEvent(new OperatorCreated(user, profileRequest.attributes()));
	}

	@Transactional
	public void updateProfile (UserId userId, ProfileRequest request)
	{
		Profile profile = getProfileByUserId(userId.id());
		request.emplace(profile);
		profileRepository.save(profile);
		eventPublisher.publishEvent(profile.getLocation());
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Location getUserLocation(Long userId)
	{
		Profile profile = profileRepository.findByUserId(userId).orElseThrow();
		return profile.getLocation();
	}
}
