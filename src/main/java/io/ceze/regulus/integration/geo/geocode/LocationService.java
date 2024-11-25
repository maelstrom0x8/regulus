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
package io.ceze.regulus.integration.geo.geocode;

import io.ceze.regulus.integration.geo.GeoService;
import io.ceze.regulus.user.domain.model.Location;
import io.ceze.regulus.user.dto.LocationData;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocationService {

    private final GeoService geoService;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public LocationService(GeoService geoService) {
        this.geoService = geoService;
    }

    @Transactional
    public Location createLocation(Location location) {
        var latLng = geoService.getCoordinates(LocationData.from(location));
        Point point =
                geometryFactory.createPoint(new Coordinate(latLng.latitude(), latLng.longitude()));
        //        location.setCoordinates(point);

        return location;
    }
}
