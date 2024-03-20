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

import io.ceze.regulus.commons.Location;
import io.ceze.regulus.generator.model.Disposal;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

@ApplicationScoped
public class ClusterManager {

  // TODO: Fix this. Label priority has higher precedence over request priority
  private final Comparator<Disposal> priorityComparator =
      Comparator.comparingInt(e -> e.getDisposalInfo().getPriority().getLevel());

  private final Map<String, Queue<Disposal>> clusterQueueMap = new ConcurrentHashMap<>();
  private final Map<String, Instant> clusterStartTimeMap = new ConcurrentHashMap<>();
  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
  private final Duration maxWaitTime = Duration.ofMinutes(10); // TODO: Externalize this

  public ClusterManager() {
    executorService.scheduleAtFixedRate(this::checkClusterWaitTimes, 1, 1, TimeUnit.MINUTES);
  }

  @NotNull
  public void add(Disposal disposal) {
    Location location = disposal.getLocation();
    if (contains(location)) return;

    String cluster = String.format("%s-%d", location.getCity(), location.getId());

    Queue<Disposal> disposalRequests =
        clusterQueueMap.computeIfAbsent(
            cluster, k -> new PriorityBlockingQueue<>(11, priorityComparator));
    disposalRequests.add(disposal);
    clusterStartTimeMap.putIfAbsent(cluster, Instant.now());
  }

  public Queue<Disposal> get(@NotNull String cluster) {
    return clusterQueueMap.get(cluster);
  }

  /**
   * @param location Location of a request
   * @return true if a location is in a cluster otherwise false
   */
  public boolean contains(Location location) {
    return clusterQueueMap.values().stream()
        .flatMap(Queue::stream)
        .map(Disposal::getLocation)
        .anyMatch(e -> e.getId().equals(location.getId()));
  }

  private void checkClusterWaitTimes() {
    Instant now = Instant.now();
    for (Map.Entry<String, Instant> entry : clusterStartTimeMap.entrySet()) {
      String cluster = entry.getKey();
      Instant startTime = entry.getValue();
      Duration elapsedTime = Duration.between(startTime, now);

      // If elapsed time exceeds max wait time, trigger dispatch
      if (elapsedTime.compareTo(maxWaitTime) >= 0) {
        dispatchCluster(cluster);
        // Remove the cluster start time entry to avoid re-dispatching
        clusterStartTimeMap.remove(cluster);
      }
    }
  }

  private void dispatchCluster(String cluster) {
    // Dispatch logic goes here
    Queue<Disposal> disposalRequests = clusterQueueMap.remove(cluster);
    // Implement dispatch logic based on the disposal requests in the cluster
  }

  @PreDestroy
  public void shutdown() {
    executorService.shutdown();
  }
}
