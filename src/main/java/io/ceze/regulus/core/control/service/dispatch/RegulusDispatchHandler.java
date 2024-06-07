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

import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.core.control.model.CollectorAgent;
import io.ceze.regulus.core.control.model.CollectorAgents;
import io.ceze.regulus.core.control.repository.CollectorRepository;
import io.ceze.regulus.core.control.service.cluster.Cluster;
import io.ceze.regulus.integration.geo.GeoData;
import io.ceze.regulus.integration.geo.PointToPoint;
import io.ceze.regulus.integration.geo.Route;
import io.ceze.regulus.integration.geo.RouteFinder;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles the dispatching of collector agents within a cluster.
 */
@DispatchProvider
@Alternative
public class RegulusDispatchHandler implements DispatchHandler {

    @Inject CollectorRepository collectorRepository;

    @Inject RouteFinder routeFinder;

    public RegulusDispatchHandler() {}

    /**
     * Dispatches tasks to collector agents within the given cluster.
     * @param cluster The cluster containing locations to be dispatched.
     */
    public void dispatch(Cluster cluster) {
        Set<CollectorAgent> collectorAgents = checkAvailableAgents(cluster.getCity());
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
    }

    private Set<CollectorAgent> checkAvailableAgents(String city) {
        return collectorRepository.findAllByCity(city).stream()
                .filter(e -> e.getLocation().getCity().equals(city))
                .flatMap(e -> e.getCollectorAgents().stream())
                .filter(CollectorAgent::isAvailable)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the nearest collector agent to the given location from a collection of agents.
     * @param agents The collection of collector agents.
     * @param location The location for which the nearest agent is sought.
     * @return The nearest collector agent to the given location.
     */
    CollectorAgent getNearestAgent(Collection<CollectorAgent> agents, Location location) {
        // Implementation is missing. It should calculate the nearest agent based on location.
        return null;
    }
}
