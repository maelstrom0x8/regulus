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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.core.control.service.dispatch.DispatchHandler;
import io.ceze.regulus.generator.model.Disposal;
import io.ceze.regulus.generator.model.DisposalInfo;
import io.ceze.regulus.generator.model.Label;
import io.ceze.regulus.generator.service.DisposalStatus;
import io.ceze.regulus.generator.web.Priority;
import java.lang.reflect.Field;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClusterManagerTest {

    @InjectMocks private ClusterManager clusterManager;

    private final DispatchHandler dispatchHandler = mock(DispatchHandler.class);
    private final MockedStatic<Haversine> haversine = mockStatic(Haversine.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        haversine.close();
    }

    @Test
    void disposalsFromSameOriginAreInTheSameCluster() {
        Disposal disposal1 =
                new Disposal(
                        Label.MSW,
                        DisposalStatus.PENDING,
                        new DisposalInfo.Builder().priority(Priority.MEDIUM).weight(3042).build());
        Disposal disposal2 =
                new Disposal(
                        Label.MSW,
                        DisposalStatus.PENDING,
                        new DisposalInfo.Builder().priority(Priority.MEDIUM).weight(3042).build());

        Location location1 = makeLocation(23L);
        location1.setState("state");
        location1.setCity("city");
        Location location2 = makeLocation(543L);
        location2.setState("state");
        location2.setCity("city");

        disposal1.setLocation(location1);
        disposal2.setLocation(location2);

        Cluster c1 = clusterManager.add(disposal1);
        Cluster c2 = clusterManager.add(disposal2);

        haversine
                .when(() -> Haversine.distance(any(Location.class), any(Location.class)))
                .thenReturn(1.5);

        assertEquals(1.5, Haversine.distance(location1, location2));

        assertTrue(c1 != null && c2 != null);
        assertEquals(c1, c2);
        assertTrue(clusterManager.activeClusters().contains(c1));
    }

    @Test
    void clusterIsDispatchedAfterWaitTimeExpires() {
        Disposal disposal =
                new Disposal(
                        Label.MSW,
                        DisposalStatus.PENDING,
                        new DisposalInfo.Builder().priority(Priority.MEDIUM).weight(3042).build());

        Location location = makeLocation(23L);
        location.setState("state");
        location.setCity("city");
        disposal.setLocation(location);

        clusterManager.setMaxWaitTime(Duration.ofMillis(500));

        Cluster cluster = clusterManager.add(disposal);

        verify(dispatchHandler, timeout(Duration.ofSeconds(90).toMillis()).times(1))
                .dispatch(cluster);
        assertFalse(clusterManager.activeClusters().contains(cluster));
    }

    private Location makeLocation(Long id) {
        Location location = new Location();
        try {
            Field idField = Location.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(location, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return location;
    }
}
