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
import io.ceze.regulus.core.generator.payload.model.*;
import io.ceze.regulus.user.domain.model.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClusterManagerTest
{

	private final MockedStatic<Haversine> haversine = mockStatic(Haversine.class);
	@InjectMocks
	private ClusterManager clusterManager;
	@Mock
	private DispatchHandler dispatchHandler;

	@AfterEach
	void tearDown()
	{
		haversine.close();
	}

	@Test
	void disposalsFromSameOriginAreInTheSameCluster()
	{
		Payload payload1 =
			new Payload(
				Label.MSW,
				PayloadStatus.PENDING,
				new PayloadInfo.Builder().priority(Priority.MEDIUM).weight(3042).build());
		Payload payload2 =
			new Payload(
				Label.MSW,
				PayloadStatus.PENDING,
				new PayloadInfo.Builder().priority(Priority.MEDIUM).weight(3042).build());

		Location location1 = makeLocation(23L);
		location1.setState("state");
		location1.setCity("city");
		Location location2 = makeLocation(543L);
		location2.setState("state");
		location2.setCity("city");

		payload1.setLocation(location1);
		payload2.setLocation(location2);

		Cluster c1 = clusterManager.add(payload1);
		Cluster c2 = clusterManager.add(payload2);

		haversine
			.when(() -> Haversine.distance(any(Location.class), any(Location.class)))
			.thenReturn(1.5);

		assertEquals(1.5, Haversine.distance(location1, location2));

		assertTrue(c1 != null && c2 != null);
		assertEquals(c1, c2);
		assertTrue(clusterManager.getClusters().contains(c1));
	}

	@Test
	void clusterIsDispatchedAfterWaitTimeExpires()
	{
		Payload payload =
			new Payload(
				Label.MSW,
				PayloadStatus.PENDING,
				new PayloadInfo.Builder().priority(Priority.MEDIUM).weight(3042).build());

		Location location = makeLocation(23L);
		location.setState("state");
		location.setCity("city");
		payload.setLocation(location);

		clusterManager.setMaxWaitTime(Duration.ofMillis(100));

		Cluster cluster = clusterManager.add(payload);

		TimerTask timerTask =
			new TimerTask()
			{
				@Override
				public void run()
				{
					clusterManager.checkClusterWaitTimes();
				}
			};

		Timer timer = new Timer(true);
		timer.schedule(timerTask, 300, 100);

		verify(dispatchHandler, timeout(Duration.ofSeconds(5).toMillis()).times(1))
			.dispatch(cluster);
	}

	private Location makeLocation(Long id)
	{
		Location location = new Location();
		try
		{
			Field idField = Location.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(location, id);
		} catch (NoSuchFieldException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}

		return location;
	}
}
