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
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ProfileRequest(LocationInfo locationInfo, Map<String, Object> attributes)
{

	public void emplace(@NotNull Profile profile)
	{
		profile.addAttributes(attributes);
		locationInfo.emplace(profile.getLocation());
	}

	public record LocationInfo(
		String streetNumber,
		String streetName,
		String city,
		String state,
		String postalCode,
		String country)
	{

		public void emplace(@NotNull Location location)
		{
			location.setStreetNumber(streetNumber);
			location.setCity(city);
			location.setStreet(streetName);
			location.setState(state);
			location.setPostalCode(postalCode);
			location.setCountry(country);
		}
	}
}
