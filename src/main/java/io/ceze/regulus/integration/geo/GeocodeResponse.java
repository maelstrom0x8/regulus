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
package io.ceze.regulus.integration.geo;

import java.util.List;

public record GeocodeResponse(Summary summary, List<Result> results) {

    public record Summary(
            String query,
            String queryType,
            int queryTime,
            int numResults,
            int offset,
            int totalResults,
            int fuzzyLevel) {}

    public record Result(
            String type,
            String id,
            double score,
            MatchConfidence matchConfidence,
            Address address,
            Position position,
            List<Mapcode> mapcodes,
            Viewport viewport,
            List<EntryPoint> entryPoints,
            AddressRanges addressRanges,
            DataSources dataSources) {}

    public record MatchConfidence(int score) {}

    public record Address(
            String streetNumber,
            String streetName,
            String municipalitySubdivision,
            String municipality,
            String countrySecondarySubdivision,
            String countryTertiarySubdivision,
            String countrySubdivision,
            String postalCode,
            String extendedPostalCode,
            String countryCode,
            String country,
            String countryCodeISO3,
            String freeformAddress,
            String countrySubdivisionName,
            String localName) {}

    public record Position(double lat, double lon) {}

    public record Mapcode(String type, String fullMapcode, String territory, String code) {}

    public record Viewport(TopLeftPoint topLeftPoint, BtmRightPoint btmRightPoint) {}

    public record TopLeftPoint(double lat, double lon) {}

    public record BtmRightPoint(double lat, double lon) {}

    public record EntryPoint(String type, Position position) {}

    public record AddressRanges(String rangeLeft, String rangeRight, From from, To to) {}

    public record From(double lat, double lon) {}

    public record To(double lat, double lon) {}

    public record DataSources(Geometry geometry) {}

    public record Geometry(String id) {}
}
