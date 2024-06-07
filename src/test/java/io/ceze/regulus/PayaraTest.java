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
package io.ceze.regulus;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class PayaraTest {

    MountableFile target = MountableFile.forHostPath(
        Paths.get("target/regulus.war").toAbsolutePath(), 0777);

    protected GenericContainer<?> payaraMicro = new GenericContainer<>(DockerImageName.parse("payara/micro:6.2024.2-jdk21"))
        .withExposedPorts(8080)
        .withCopyFileToContainer(target, "/opt/payara/deployments/app.war")
        .withCommand("--deploy /opt/payara/deployments/app.war --contextRoot /");

    @BeforeEach
    void setUp() {
        payaraMicro.start();
    }

    @AfterEach
    void tearDown() {
        payaraMicro.stop();
    }

    @Test
    void containerIsStarted() {
        assertTrue(payaraMicro.isRunning());
    }

}
