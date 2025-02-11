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
 *
 */
package io.ceze.regulus.core.cluster;

import io.ceze.regulus.core.dispatch.DispatchHandler;
import io.ceze.regulus.core.generator.payload.model.Payload;
import io.ceze.regulus.event.NewPayloadEvent;
import io.ceze.regulus.user.domain.model.Location;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class ClusterManager
{

	private static final double MAX_CLUSTER_DISTANCE = 2.0;
	private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);
	private final DispatchHandler dispatchHandler;
	private final Queue<Cluster> clusters =
		new PriorityBlockingQueue<>(11, Comparator.comparing(Cluster::getWaitUntil));
	private Duration maxWaitTime = Duration.ofMinutes(1); // TODO: Externalize this

	public ClusterManager(DispatchHandler dispatchHandler)
	{
		this.dispatchHandler = dispatchHandler;
	}

	public void setMaxWaitTime(Duration maxWaitTime)
	{
		this.maxWaitTime = maxWaitTime;
	}

	public Cluster add(@NotNull Payload payload)
	{
		Location location = payload.getLocation();
		if (contains(location))
		{
			log.warn("Location with id {} is already in an active cluster", location.getId());
			return null;
		}

		Cluster cluster = find(location);
		long secs = maxWaitTime.toSeconds();
		log.info("Adding payload with id {} to the cluster {} for {}s", payload.getId(), cluster.getName(), secs);
		cluster.add(payload);
		cluster.setWaitTime(secs);

		clusters.add(cluster);
		return cluster;
	}

	public Collection<Cluster> getClusters()
	{
		return List.copyOf(clusters);
	}

	/**
	 * @param location Location of a request
	 * @return true if a location is in a cluster otherwise false
	 */
	public boolean contains(@NotNull Location location)
	{
		return clusters.stream()
			.flatMap(c -> c.getRequestQueue().stream())
			.map(Payload::getLocation)
			.anyMatch(e -> e.getId().equals(location.getId()));
	}

	@Scheduled(initialDelay = 1L, fixedRate = 1L, timeUnit = TimeUnit.MINUTES)
	public void checkClusterWaitTimes()
	{
		log.info("Fetching expired clusters");
		log.info("Clusters: {}", clusters);
		clusters.stream().filter(Cluster::waitIsExpired).forEach(this::dispatchCluster);
	}

	private void dispatchCluster(Cluster cluster)
	{
		log.info("Dispatching cluster {}", cluster.getName());
		if (dispatchHandler.dispatch(cluster))
		{
			clusters.remove(cluster);
		}
	}

	/**
	 * Search for the cluster containing the location
	 *
	 * @param location Location
	 * @return Found cluster or null if cluster is not found
	 */
	public Cluster find(Location location)
	{
		var cluster =
			getClusters().stream()
				.filter(
					c ->
						Haversine.distance(c.getOrigin(), location)
							< MAX_CLUSTER_DISTANCE)
				.findFirst();
		return cluster.orElseGet(() -> new Cluster(location));
	}

	@Async
	@TransactionalEventListener
	public void newPayloadHandler(NewPayloadEvent event)
	{
		log.warn("New payload received {}", event.payload().getId());
		add(event.payload());
	}
}
