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
package io.ceze.regulus.user.dto;

import io.ceze.regulus.user.domain.model.Location;
import io.ceze.regulus.user.domain.model.Profile;

import java.time.LocalDateTime;

public record ProfileResponse(
	Long profileId,
	String email,
	LocalDateTime created,
	LocalDateTime lastModified,
	ProfileRequest.LocationInfo locationInfo)
{

	public static ProfileResponse from(Profile profile)
	{
		Location location = profile.getLocation();
		return new ProfileResponse(
			profile.getId(),
			profile.getUser().getEmail(),
			profile.getCreatedAt(),
			profile.getLastModified(),
			new ProfileRequest.LocationInfo(
				location.getStreetNumber(),
				location.getStreet(),
				location.getCity(),
				location.getState(),
				location.getPostalCode(),
				location.getCountry()));
	}
}
