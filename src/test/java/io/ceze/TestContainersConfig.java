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
package io.ceze;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig
{

	public static void init()
	{
		GenericContainer<?> mailHog =
			new GenericContainer<>(DockerImageName.parse("mailhog/mailhog:v1.0.1"))
				.withExposedPorts(1025, 8025);
		mailHog.start();
		System.setProperty("spring.mail.host", mailHog.getHost());
		System.setProperty("spring.mail.port", String.valueOf(mailHog.getMappedPort(1025)));
	}

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgres()
	{
		return new PostgreSQLContainer<>(
			DockerImageName.parse("postgis/postgis:17-3.5-alpine")
				.asCompatibleSubstituteFor("postgres"))
			.withTmpFs(Collections.singletonMap("/test/tmpfs", "rw"));
	}

	@Bean
	ObjectMapper objectMapper()
	{
		return new ObjectMapper();
	}
}
