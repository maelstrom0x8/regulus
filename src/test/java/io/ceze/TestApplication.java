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

import io.ceze.commons.AbstractIT;
import io.ceze.regulus.RegulusApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(
	locations = "classpath:application-test.yml",
	inheritProperties = false,
	inheritLocations = false)
public class TestApplication extends AbstractIT
{

	public static void main(String... args)
	{
		SpringApplication.from(RegulusApplication::main).with(TestContainersConfig.class).run(args);
	}
}
