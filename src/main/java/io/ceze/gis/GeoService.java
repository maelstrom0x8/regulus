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
package io.ceze.gis;

import io.ceze.regulus.user.dto.LocationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;

@Service
public class GeoService
{

	@Value("${regulus.providers.gis.base-url}")
	private String baseUrl;

	private static final Logger log = LoggerFactory.getLogger(GeoService.class);
	private final GeoApiContext context;
	private final RestTemplate restTemplate;

	public GeoService (GeoApiContext context, RestTemplate restTemplate)
	{
		this.context = context;
		this.restTemplate = restTemplate;
	}

	public LatLng getCoordinates (LocationData locationData)
	{
		final String apiKey = context.getApiKey();

		log.info("Fetching coordinates for {}", locationData);

		var response = restTemplate.getForEntity(baseUrl + "/search/2/geocode/{query}.json?key={key}",
			GeocodeResponse.class, locationData.toString(), apiKey);

		if (!response.getStatusCode().is2xxSuccessful())
			log.error("Error fetching coordinates for {}", locationData);

		GeocodeResponse geocodeResponse = response.getBody();

		Optional<GeocodeResponse.Result> result = Objects.requireNonNull(geocodeResponse).results().stream().findFirst();
		if(result.isPresent())
		{
			log.info("Found coordinates for location {}:", locationData);
			GeocodeResponse.Result.Position position = result.get().position();
			return new LatLng(position.lat(), position.lon());
		}
		return null;
	}
}
