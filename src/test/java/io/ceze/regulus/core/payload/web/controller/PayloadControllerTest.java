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
package io.ceze.regulus.core.payload.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ceze.config.security.AuthenticatedUser;
import io.ceze.config.security.AuthenticationService;
import io.ceze.regulus.core.generator.payload.model.*;
import io.ceze.regulus.core.generator.payload.repository.PayloadRepository;
import io.ceze.regulus.core.generator.payload.service.*;
import io.ceze.regulus.core.generator.web.controller.PayloadController;
import io.ceze.regulus.event.CancelledPayloadEvent;
import io.ceze.regulus.user.domain.model.Location;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.service.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(
	controllers = PayloadController.class,
	properties = "spring.security.oauth2.resourceserver.jwt.issuer_uri=test")
public class PayloadControllerTest
{

	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private MockMvc mvc;
	@MockBean
	private PayloadService payloadService;
	@MockBean
	PayloadRepository payloadRepository;
	@MockBean
	private UserService userService;
	@MockBean
	private AuthenticationService authenticationService;
	@MockBean
	ApplicationEventPublisher eventPublisher;

	@Test
	void shouldCreateNewPayloadRequest () throws Exception
	{
		UserId id = new UserId(99L, "ena@foo.com");
		PayloadRequest request = new PayloadRequest(Label.MSW, 781, Priority.MEDIUM);
		PayloadResponse response =
			new PayloadResponse(
				545L,
				Label.MSW,
				PayloadStatus.PENDING,
				new PayloadInfo.Builder().priority(Priority.MEDIUM).weight(781).build(),
				LocalDateTime.now(),
				LocalDateTime.now(),
				1000L);

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser(id.email(), null));
		when(userService.getUserByEmail(anyString())).thenReturn(id);
		when(payloadService.initiatePayloadRequest(id, request)).thenReturn(response);

		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		mvc.perform(
				post("/v1/payloads")
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities))
						.authorities(authorities))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is2xxSuccessful());

		verify(payloadService, times(1)).initiatePayloadRequest(id, request);
	}

	@Test
	void shouldNotHandleRequestForNonAccountHolders () throws Exception
	{

		PayloadRequest request = new PayloadRequest(Label.HAZARDOUS, 4354, Priority.URGENT);

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser("ena@foo.com", null));
		when(userService.getUserByEmail(anyString())).thenReturn(null);
		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		var result = mvc.perform(
				post("/v1/payloads")
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities))
						.authorities(authorities))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andReturn();

		String location = result.getResponse().getHeader("Location");

		assertThat(result.getResponse().getStatus()).isEqualTo(401);
		assertThat(location).isNotBlank();
	}


	@Test
	void shouldReturnBadRequestForDuplicateDisposalRequest () throws Exception
	{
		UserId id = new UserId(99L, "ena@foo.com");
		PayloadRequest request = new PayloadRequest(Label.MSW, 781, Priority.MEDIUM);

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser(id.email(), null));
		when(userService.getUserByEmail(anyString())).thenReturn(id);
		when(payloadService.initiatePayloadRequest(id, request))
			.thenThrow(new DuplicateRequestException("Payload request already initiated at this location"));

		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		mvc.perform(
				post("/v1/payloads")
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities))
						.authorities(authorities))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@Disabled
	void shouldCancelPayloadRequestSuccessfullyIfStillPending () throws Exception
	{
		UserId id = new UserId(99L, "ena@foo.com");
		Long payloadId = 545L;
		Payload payload = new Payload();
		payload.setStatus(PayloadStatus.PENDING);

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser(id.email(), null));
		when(userService.getUserByEmail(anyString())).thenReturn(id);
		when(payloadService.getPayloadById(new PayloadId(id.id(), payloadId)))
			.thenReturn(payload);

		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		mvc.perform(
				delete("/v1/payloads/cancel/{id}", payloadId)
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities))
						.authorities(authorities)))
			.andExpect(status().isOk());

		assertThat(payload.getStatus()).isEqualTo(PayloadStatus.CANCELLED);
		verify(payloadService, times(1)).cancelPayloadRequest(new PayloadId(id.id(), payloadId));
		verify(eventPublisher, times(1)).publishEvent(any(CancelledPayloadEvent.class));
	}

	@Test
	void shouldFetchPayloadByIdWithGivenIdAndUserId () throws Exception
	{
		UserId userId = new UserId(99L, "ena@foo.com");
		Long payloadId = 545L;
		Location location = new Location("21", "street", "city", "state", "993", "NG");
		Payload payload = new Payload(Label.MSW, PayloadStatus.PROCESSED, new PayloadInfo.Builder()
			.weight(323).priority(Priority.URGENT)
			.build());
		payload.setLocation(location);
		ReflectionTestUtils.setField(payload, "id", payloadId);

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser(userId.email(), null));
		when(userService.getUserByEmail(anyString())).thenReturn(userId);
		when(payloadService.getPayloadById(new PayloadId(userId.id(), payloadId)))
			.thenReturn(payload);

		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		mvc.perform(
				get("/v1/payloads/" + payloadId)
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities))
						.authorities(authorities))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(PayloadResponse.from(payload))));

		verify(payloadService, times(1)).getPayloadById(new PayloadId(userId.id(), payloadId));
	}

	@Test
	void shouldReturnNotFoundForInvalidPayloadId () throws Exception
	{
		UserId userId = new UserId(99L, "ena@foo.com");
		Long payloadId = 545L;

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser(userId.email(), null));
		when(userService.getUserByEmail(anyString())).thenReturn(userId);
		when(payloadService.getPayloadById(new PayloadId(userId.id(), payloadId)))
			.thenThrow(PayloadNotFoundException.class);

		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		mvc.perform(
				get("/v1/payloads/" + payloadId)
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities))
						.authorities(authorities))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());

		verify(payloadService, times(1)).getPayloadById(new PayloadId(userId.id(), payloadId));
	}

	@Test
	void noopWhenCancellingInProcessPayloadRequest () throws Exception
	{
		UserId userId = new UserId(99L, "ena@foo.com");
		Long payloadId = 545L;
		Payload payload = new Payload(Label.MSW, PayloadStatus.IN_PROCESS, new PayloadInfo.Builder()
			.weight(100).priority(Priority.LOW)
			.build());

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser(userId.email(), null));
		when(userService.getUserByEmail(anyString())).thenReturn(userId);
		when(payloadService.getPayloadById(new PayloadId(userId.id(), payloadId)))
			.thenReturn(payload);

		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		mvc.perform(
				delete("/v1/payloads/cancel/" + payloadId)
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities))
						.authorities(authorities))
					.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		verify(eventPublisher, times(0)).publishEvent(any(CancelledPayloadEvent.class));
	}

	@Test
	@Disabled
	void shouldUpdatePayloadSuccessfully () throws Exception
	{
		UserId id = new UserId(99L, "ena@foo.com");
		Payload payload = new Payload(Label.MEDICAL, PayloadStatus.PENDING, new PayloadInfo.Builder()
			.weight(422).priority(Priority.MEDIUM).build());
		ReflectionTestUtils.setField(payload, "id", 545L);
		PayloadRequest request = new PayloadRequest(Label.MEDICAL, 781, Priority.URGENT);

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser(id.email(), null));
		when(userService.getUserByEmail(anyString())).thenReturn(id);
		when(payloadService.getPayloadById(any(PayloadId.class))).thenReturn(payload);

		Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_GENERATOR"));

		mvc.perform(
				put("/v1/payloads/{id}", 545L)
					.with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities))
						.authorities(authorities))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk());

		assertThat(payload.getStatus()).isEqualTo(PayloadStatus.PENDING);
		assertThat(payload.getPayloadInfo().getPriority().name()).isEqualTo(Priority.URGENT);
		assertThat(payload.getPayloadInfo().getWeight()).isEqualTo(781);

	}
}
