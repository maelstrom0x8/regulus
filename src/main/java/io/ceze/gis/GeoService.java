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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeoService {
    private static final Logger log = LoggerFactory.getLogger(GeoService.class);
    private final GeoApiContext context;
    private final RestTemplate restTemplate;

    public GeoService(GeoApiContext context, RestTemplate restTemplate) {
        this.context = context;
        this.restTemplate = restTemplate;
    }

    public LatLng getCoordinates(LocationData locationData) {
        final String apiKey = context.getApiKey();
        // @fixme fix url construction
        UriComponents components =
                UriComponentsBuilder.fromHttpUrl("https://api.tomtom.com")
                        .path("/search/2/structuredGeocode.json")
                        .queryParam("key", apiKey)
                        .queryParam("streetName", locationData.street())
                        .queryParam("streetNumber", locationData.number())
                        .queryParam("postalCode", locationData.zipCode())
                        .queryParam("municipality", locationData.city())
                        .queryParam("countryCode", locationData.country())
                        .build();

        var response = restTemplate.getForObject(components.toUri(), GeocodeResponse.class);

        var sortedResults =
                response.results().stream()
                        .sorted((r1, r2) -> Double.compare(r2.score(), r1.score()))
                        .toList();

        if (!sortedResults.isEmpty()) {
            var bestResult = sortedResults.get(0);
            return new LatLng(bestResult.position().lat(), bestResult.position().lon());
        }
        return null;
    }
}
