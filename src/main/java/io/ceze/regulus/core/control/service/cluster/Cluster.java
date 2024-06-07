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
import io.ceze.regulus.generator.model.Disposal;
import java.util.Comparator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import org.jboss.logging.Logger;

public class Cluster {

    private static final Logger LOG = Logger.getLogger(Cluster.class);

    // TODO: Fix this. Label priority has higher precedence over request priority
    private final Comparator<Disposal> priorityComparator =
            Comparator.comparingInt(e -> e.getDisposalInfo().getPriority().getLevel());

    private final Queue<Disposal> requestQueue =
            new PriorityBlockingQueue<>(11, priorityComparator);

    private final String name;
    private Location origin;

    public Cluster(Location location) {
        String clusterName =
                String.join("-", location.getState(), location.getCity(), nameSuffix())
                        .toLowerCase();
        LOG.infof("Creating new cluster %s", clusterName);
        this.name = clusterName;
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

    private String nameSuffix() {
        String s = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(s.length());
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(s.length());
            sb.append(s.charAt(index));
        }
        return sb.toString();
    }
}
