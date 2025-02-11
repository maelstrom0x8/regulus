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

import io.ceze.regulus.core.generator.payload.model.Payload;
import io.ceze.regulus.user.domain.model.Location;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

public class Cluster
{

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(Cluster.class);
	// TODO: Fix this. Label priority has higher precedence over request priority
	private final Comparator<Payload> priorityComparator =
		Comparator.comparingInt(e -> e.getPayloadInfo().getPriority().getLevel());

	private final Queue<Payload> requestQueue =
		new PriorityBlockingQueue<>(11, priorityComparator);

	private final String name;
	private Location origin;
	private Instant waitUntil;

	public Cluster(Location origin, long waitFor)
	{
		this(origin);
		this.waitUntil = Instant.now().plus(waitFor, ChronoUnit.SECONDS);
	}

	public Cluster(Location location)
	{
		String clusterName =
			String.join("-", location.getState(), location.getCity(), nameSuffix())
				.toLowerCase()
				.replace(' ', '.');
		log.info("Creating new cluster {}", clusterName);
		this.name = clusterName;
	}

	boolean waitIsExpired()
	{
		Duration between = Duration.between(Instant.now(), waitUntil);
		return between.isNegative();
	}

	public String getName()
	{
		return name;
	}

	public Queue<Payload> getRequestQueue()
	{
		return requestQueue;
	}

	public void add(Payload payload)
	{
		if (requestQueue.isEmpty())
		{
			this.origin = payload.getLocation();
		}
		this.requestQueue.add(payload);
	}

	public String getCity()
	{
		return origin.getCity();
	}

	public Location getOrigin()
	{
		return this.origin;
	}

	private String nameSuffix()
	{
		String s = "abcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder(s.length());
		Random random = new Random();
		for (int i = 0; i < 5; i++)
		{
			int index = random.nextInt(s.length());
			sb.append(s.charAt(index));
		}
		return sb.toString();
	}

	public Instant getWaitUntil()
	{
		return waitUntil;
	}

	public void setWaitTime(long seconds)
	{
		this.waitUntil = Instant.now().plus(seconds, ChronoUnit.SECONDS);
	}
}
