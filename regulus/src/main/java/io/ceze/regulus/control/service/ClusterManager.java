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
package io.ceze.regulus.control.service;

import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.control.service.cluster.Cluster;
import io.ceze.regulus.generator.model.Disposal;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ClusterManager {

  @Inject DispatchHandler dispatchHandler;

  private static final Logger LOG = LoggerFactory.getLogger(ClusterManager.class);

  private final Map<String, Cluster> clusterQueueMap = new ConcurrentHashMap<>();
  private final Map<String, Instant> clusterStartTimeMap = new ConcurrentHashMap<>();
  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
  private final Duration maxWaitTime = Duration.ofMinutes(10); // TODO: Externalize this

  public ClusterManager() {
    Class<? extends DispatchHandler> handlerClass = dispatchHandler.getClass();
    LOG.info("Dispatch managed by {}", handlerClass.getCanonicalName());
    executorService.scheduleAtFixedRate(this::checkClusterWaitTimes, 1, 1, TimeUnit.MINUTES);
  }

  public void add(@NotNull Disposal disposal) {
    Location location = disposal.getLocation();
    if (contains(location)) return;

    String cluster = String.format("%s-%d", location.getCity(), location.getId());

    Cluster disposalRequests = clusterQueueMap.computeIfAbsent(cluster, Cluster::new);
    disposalRequests.add(disposal);
    clusterStartTimeMap.putIfAbsent(cluster, Instant.now());
  }

  public Cluster get(@NotNull String cluster) {
    return clusterQueueMap.get(cluster);
  }

  /**
   * @param location Location of a request
   * @return true if a location is in a cluster otherwise false
   */
  public boolean contains(@NotNull Location location) {
    return clusterQueueMap.values().stream()
        .flatMap(c -> c.getRequestQueue().stream())
        .map(Disposal::getLocation)
        .anyMatch(e -> e.getId().equals(location.getId()));
  }

  private void checkClusterWaitTimes() {
    Instant now = Instant.now();
    for (Map.Entry<String, Instant> entry : clusterStartTimeMap.entrySet()) {
      String cluster = entry.getKey();
      Instant startTime = entry.getValue();
      Duration elapsedTime = Duration.between(startTime, now);

      if (elapsedTime.compareTo(maxWaitTime) >= 0) {
        LOG.info("Cluster {} wait-time expired", cluster);
        dispatchCluster(cluster);
        clusterStartTimeMap.remove(cluster);
      }
    }
  }

  private void dispatchCluster(String clusterKey) {
    Cluster cluster = clusterQueueMap.remove(clusterKey);
    LOG.info("Dispatching cluster {}", clusterKey);
    dispatchHandler.dispatch(cluster);
  }

  @PreDestroy
  public void shutdown() {
    executorService.shutdown();
  }
}
