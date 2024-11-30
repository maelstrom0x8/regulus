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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.ceze.commons.AbstractIT;
import io.ceze.config.RegulusProperties;
import io.ceze.regulus.core.generator.payload.model.Label;
import io.ceze.regulus.core.generator.payload.model.Priority;
import io.ceze.regulus.core.generator.payload.service.PayloadRequest;
import io.ceze.regulus.core.processing.model.PayloadType;
import io.ceze.regulus.core.processing.repository.OperatorRepository;
import io.ceze.regulus.user.domain.model.Role;
import io.ceze.regulus.user.dto.NewUserRequest;
import io.ceze.regulus.user.dto.ProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "regulus.providers.gis.api-key=test")
public class RegulusApplicationTest extends AbstractIT
{

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private RegulusProperties regulusProperties;

	@Autowired
	OperatorRepository operatorRepository;

	@BeforeEach
	void setUp ()
	{
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Test
	void contextLoads () throws Exception
	{
		mvc.perform(get("/actuator/health")).andExpect(status().is2xxSuccessful());
	}

	@Transactional
	@Test
	void canCreateUserAccount () throws Exception
	{

		NewUserRequest body = new NewUserRequest("ena@foo.com", Role.GENERATOR, null, null);
		mvc.perform(
				post("/v1/users/register")
					.content(objectMapper.writeValueAsString(body))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(200));
	}

	@Test
	@Transactional
	void createUserProfile () throws Exception
	{
		NewUserRequest body = new NewUserRequest("ena@foo.com", Role.GENERATOR, null, null);
		mvc.perform(
				post("/v1/users/register")
					.content(objectMapper.writeValueAsString(body))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(200));

		ProfileRequest profileRequest =
			new ProfileRequest(
				new ProfileRequest.LocationInfo(
					"221B", "Baker Street", "Oxford", "London", "12333", "UK"),
				Map.of("first_name", "Elena",
					"last_name", "Xe",
					"date_of_birth", LocalDate.of(1987, 2, 17).toString())
			);
		mvc.perform(
				post("/v1/profiles")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(profileRequest))
					.with(jwt().jwt(j -> j.subject("ena@foo.com"))))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	@Transactional
	void newPayloadRequest () throws Exception
	{
		NewUserRequest body = new NewUserRequest("ena@foo.com", Role.GENERATOR, null, null);
		mvc.perform(
				post("/v1/users/register")
					.content(objectMapper.writeValueAsString(body))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(200));

		Map<String, Object> profileAttributes = Map.of("first_name", "Elena",
			"last_name", "Xe",
			"date_of_birth", LocalDate.of(1987, 2, 17).toString());

		ProfileRequest profileRequest =
			new ProfileRequest(
				new ProfileRequest.LocationInfo(
					"221B", "Baker Street", "Oxford", "London", "12333", "UK"),
				profileAttributes );
		mvc.perform(
				post("/v1/profiles")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(profileRequest))
					.with(jwt().jwt(j -> j.subject("ena@foo.com"))))
			.andExpect(status().is2xxSuccessful());

		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		PayloadRequest request = new PayloadRequest(Label.MSW, 91, Priority.HIGH);
		mvc.perform(
				post("/v1/payloads")
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities)).authorities(authorities))
					.content(objectMapper.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	void registeringANewCollectorCreatesAnOperator() throws Exception {
		Map<String, Object> properties = Map.of("name", "Nuevo Company", "capacity", 24233,
			"payloadType", Collections.singleton(PayloadType.RECYCLABLE), "type", "collector");
		NewUserRequest body = new NewUserRequest("nuevo@foo.com", Role.COLLECTOR, "Nuevo Company", properties);
		mvc.perform(
				post("/v1/users/register")
					.content(objectMapper.writeValueAsString(body))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().is(200));


	}
}
