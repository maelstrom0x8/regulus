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
package io.ceze.regulus.core.control.service.dispatch;

import io.ceze.gis.*;
import io.ceze.regulus.core.control.model.CollectorAgent;
import io.ceze.regulus.core.control.model.CollectorAgents;
import io.ceze.regulus.core.control.repository.CollectorRepository;
import io.ceze.regulus.core.control.service.cluster.Cluster;
import io.ceze.regulus.user.domain.model.Location;
import io.ceze.regulus.user.domain.service.ProfileService;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Handles the dispatching of collector agents within a cluster.
 */
@Component
class RegulusDispatchHandler implements DispatchHandler {

    private static final Logger log = LoggerFactory.getLogger(RegulusDispatchHandler.class);
    private final CollectorRepository collectorRepository;
    private final ProfileService profileService;
    private final RouteFinder routeFinder;

    RegulusDispatchHandler(
            CollectorRepository collectorRepository,
            ProfileService profileService,
            RouteFinder routeFinder) {
        this.collectorRepository = collectorRepository;
        this.profileService = profileService;
        this.routeFinder = routeFinder;
    }

    /**
     * Dispatches tasks to collector agents within the given cluster.
     * @param cluster The cluster containing locations to be dispatched.
     */
    @Override
    public boolean dispatch(Cluster cluster) {
        Set<CollectorAgent> collectorAgents = checkAvailableAgents(cluster.getCity());
        if (collectorAgents.isEmpty()) {
            log.warn("No collector agents found for city '{}'", cluster.getCity());
            return false;
        }
        CollectorAgent nearestAgent = getNearestAgent(collectorAgents, cluster.getOrigin());
        PointToPoint route =
                (PointToPoint)
                        routeFinder.find(
                                GeoData.from(nearestAgent.getLocation()),
                                GeoData.from(cluster.getOrigin()));

        ArrayDeque<GeoData> geoPoints =
                cluster.getRequestQueue().stream()
                        .filter(f -> f.getLocation() != cluster.getOrigin())
                        .map(e -> GeoData.from(e.getLocation()))
                        .collect(Collectors.toCollection(ArrayDeque::new));

        Route plannedRoute = routeFinder.find(route.destination(), geoPoints);
        CollectorAgents.withAgent(nearestAgent).dispatch(plannedRoute);
        return true;
    }

    private Set<CollectorAgent> checkAvailableAgents(String city) {
        // @TODO Move this to the CollectionService
        return collectorRepository.findAllByLocationCity(city).stream()
                .filter(e -> profileService.getUserLocation(e.getId()).getCity().equals(city))
                .flatMap(e -> e.getCollectorAgents().stream())
                .filter(CollectorAgent::isAvailable)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the nearest collector agent to the given location from a collection of agents.
     *
     * @param agents   The collection of collector agents.
     * @param location The location for which the nearest agent is sought.
     * @return The nearest collector agent to the given location.
     */
    CollectorAgent getNearestAgent(Collection<CollectorAgent> agents, Location location) {
        return agents.stream()
                .min(
                        (a1, a2) ->
                                Double.compare(
                                        calculateDistance(a1.getLocation(), location),
                                        calculateDistance(a2.getLocation(), location)))
                .orElse(null);
    }

    private double calculateDistance(Location loc1, Location loc2) {
        // Get lat and lngs from the locations respectively
        return GeoUtils.calculateDistance(0, 0, 0, 0);
    }
}
