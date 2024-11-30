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

import java.util.List;

public record GeocodeResponse(
	Summary summary,
	List<Result> results
) {
	public record Summary(
		String query,
		String queryType,
		int queryTime,
		int numResults,
		int offset,
		int totalResults,
		int fuzzyLevel
	) {}

	public record Result(
		String type,
		String id,
		double score,
		MatchConfidence matchConfidence,
		Address address,
		Position position,
		Viewport viewport
	) {
		public record MatchConfidence(double score) {}

		public record Address(
			String streetName,
			String municipalitySubdivision,
			String municipality,
			String countrySubdivision,
			String countrySubdivisionName,
			String countrySubdivisionCode,
			String countryCode,
			String country,
			String countryCodeISO3,
			String freeformAddress,
			String localName
		) {}

		public record Position(
			double lat,
			double lon
		) {}

		public record Viewport(
			Point topLeftPoint,
			Point btmRightPoint
		) {
			public record Point(double lat, double lon) {}
		}
	}
}
