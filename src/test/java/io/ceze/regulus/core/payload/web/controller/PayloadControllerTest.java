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
import io.ceze.regulus.core.generator.payload.model.Label;
import io.ceze.regulus.core.generator.payload.model.PayloadInfo;
import io.ceze.regulus.core.generator.payload.model.PayloadStatus;
import io.ceze.regulus.core.generator.payload.model.Priority;
import io.ceze.regulus.core.generator.payload.service.PayloadRequest;
import io.ceze.regulus.core.generator.payload.service.PayloadResponse;
import io.ceze.regulus.core.generator.payload.service.PayloadService;
import io.ceze.regulus.core.generator.web.controller.PayloadController;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	private UserService userService;
	@MockBean
	private AuthenticationService authenticationService;

	@Test
	void shouldCreateNewDisposalRequest() throws Exception
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
		when(payloadService.newDisposalRequest(id, request)).thenReturn(response);

		mvc.perform(
				post("/v1/payloads")
					.with(jwt().jwt(j -> j.subject("ena@foo.com")))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is2xxSuccessful());

		verify(payloadService, times(1)).newDisposalRequest(id, request);
	}

	@Test
	void shouldNotHandleRequestForNonAccountHolders() throws Exception
	{

		PayloadRequest request = new PayloadRequest(Label.HAZARDOUS, 4354, Priority.URGENT);

		when(authenticationService.authenticated())
			.thenReturn(new AuthenticatedUser("ena@foo.com", null));
		when(userService.getUserByEmail(anyString())).thenReturn(null);

		MvcResult result =
			mvc.perform(
					post("/v1/payloads")
						.with(jwt().jwt(j -> j.subject("ena@foo.com")))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andReturn();

		String location = result.getResponse().getHeader("Location");

		assertThat(result.getResponse().getStatus()).isEqualTo(401);
		assertThat(location).isNotBlank();
	}
}
