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
import io.ceze.regulus.control.CollectorRepository;
import io.ceze.regulus.control.model.Collector;
import io.ceze.regulus.control.model.CollectorAgent;
import io.ceze.regulus.generator.model.Disposal;
import io.ceze.regulus.generator.service.DisposalResponse;
import io.ceze.regulus.generator.service.DisposalStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@ApplicationScoped
public class CollectionService {

  @Inject CollectorRepository collectorRepository;

  @ConfigProperty(name = "requestTimeout", defaultValue = "10800")
  private Duration requestWaitTimeout;

  @Inject private ClusterManager clusterManager;

  private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

  public Collector registerCollector(String name, boolean available, Location location) {
    Collector c = new Collector(name, available, location);
    Collector collector = collectorRepository.save(c);

    updateGeoData(collector.getLocation());

    return collector;
  }

  private void updateGeoData(Location location) {
    //
  }

  public DisposalResponse handleDisposal(Disposal disposal) {
    Set<CollectorAgent> agents = checkAvailableAgents(disposal.getLocation().getCity());

    if (agents.isEmpty())
      return new DisposalResponse(disposal.getId(), DisposalStatus.NO_AVAILABLE_AGENTS);

    clusterManager.add(disposal);
    return null;
  }

  private Set<CollectorAgent> checkAvailableAgents(String city) {
    return collectorRepository.findAllByCity(city).stream()
        .filter(e -> e.getLocation().getCity().equals(city))
        .flatMap(e -> e.getCollectorAgents().stream())
        .filter(CollectorAgent::isAvailable)
        .collect(Collectors.toSet());
  }
}
