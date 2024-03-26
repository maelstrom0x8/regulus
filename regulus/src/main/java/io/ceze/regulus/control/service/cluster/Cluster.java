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
package io.ceze.regulus.control.service.cluster;

import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.control.service.ClusterManager;
import io.ceze.regulus.generator.model.Disposal;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cluster {

  private static final Logger LOG = LoggerFactory.getLogger(ClusterManager.class);

  // TODO: Fix this. Label priority has higher precedence over request priority
  private final Comparator<Disposal> priorityComparator =
      Comparator.comparingInt(e -> e.getDisposalInfo().getPriority().getLevel());

  private final Queue<Disposal> requestQueue = new PriorityBlockingQueue<>(11, priorityComparator);

  private final String name;
  private Location origin;

  public Cluster(String name) {
    LOG.info("Creating new cluster {}", name);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Queue<Disposal> getRequestQueue() {
    return requestQueue;
  }

  public void add(Disposal disposal) {
    if (requestQueue.isEmpty()) {
      this.origin = disposal.getLocation();
    }
    this.requestQueue.add(disposal);
  }

  public String getCity() {
    return name.split("-")[0];
  }

  public Location getOrigin() {
    return this.origin;
  }
}
