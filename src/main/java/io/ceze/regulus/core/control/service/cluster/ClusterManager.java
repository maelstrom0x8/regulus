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
package io.ceze.regulus.core.control.service.cluster;

import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.core.control.service.dispatch.DispatchHandler;
import io.ceze.regulus.core.control.service.dispatch.NoopDispatcher;
import io.ceze.regulus.generator.model.Disposal;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jboss.logging.Logger;

public class ClusterManager {

    private static final double MAX_CLUSTER_DISTANCE = 2.0;
    @Inject @NoopDispatcher DispatchHandler dispatchHandler;

    private static final Logger LOG = Logger.getLogger(ClusterManager.class);

    private final Map<String, Cluster> clusterMap = new ConcurrentHashMap<>();
    private final Map<String, Instant> clusterStartTimeMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Duration maxWaitTime = Duration.ofMinutes(1); // TODO: Externalize this

    public void setMaxWaitTime(Duration maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public ClusterManager() {
        executorService.scheduleAtFixedRate(this::checkClusterWaitTimes, 1, 1, TimeUnit.MINUTES);
    }

    public Cluster add(@NotNull Disposal disposal) {
        Location location = disposal.getLocation();
        if (contains(location)) return null;

        Cluster cluster = find(location);
        cluster.add(disposal);

        clusterMap.put(cluster.getName(), cluster);
        clusterStartTimeMap.putIfAbsent(cluster.getName(), Instant.now());
        return cluster;
    }

    public Collection<Cluster> activeClusters() {
        return List.copyOf(clusterMap.values());
    }

    /**
     * @param location Location of a request
     * @return true if a location is in a cluster otherwise false
     */
    public boolean contains(@NotNull Location location) {
        return clusterMap.values().stream()
                .flatMap(c -> c.getRequestQueue().stream())
                .map(Disposal::getLocation)
                .anyMatch(e -> e.getId().equals(location.getId()));
    }

    private void checkClusterWaitTimes() {
        Instant now = Instant.now();
        clusterStartTimeMap.forEach(
                (cluster, startTime) -> {
                    Duration elapsedTime = Duration.between(startTime, now);

                    if (elapsedTime.compareTo(maxWaitTime) >= 0) {
                        LOG.infof("Cluster %s wait-time expired", cluster);
                        dispatchCluster(cluster);
                        clusterStartTimeMap.remove(cluster);
                    }
                });
    }

    private void dispatchCluster(String clusterKey) {
        Cluster cluster = clusterMap.remove(clusterKey);
        LOG.infof("Dispatching cluster %s", clusterKey);
        dispatchHandler.dispatch(cluster);
    }

    public Cluster find(Location location) {
        var cluster =
                activeClusters().stream()
                        .filter(
                                c ->
                                        Haversine.distance(c.getOrigin(), location)
                                                < MAX_CLUSTER_DISTANCE)
                        .findFirst();
        return cluster.orElseGet(() -> new Cluster(location));
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
