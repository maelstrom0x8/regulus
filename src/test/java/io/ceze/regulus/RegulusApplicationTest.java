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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.ceze.config.RegulusProperties;
import io.ceze.regulus.commons.AbstractIT;
import io.ceze.regulus.core.generator.model.Label;
import io.ceze.regulus.core.generator.web.DisposalRequest;
import io.ceze.regulus.core.generator.web.Priority;
import io.ceze.regulus.user.domain.model.Role;
import io.ceze.regulus.user.dto.NewUserRequest;
import io.ceze.regulus.user.dto.ProfileRequest;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(properties = "regulus.providers.gis.api-key=test")
public class RegulusApplicationTest extends AbstractIT {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private RegulusProperties regulusProperties;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void contextLoads() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().is2xxSuccessful());
    }

    @Transactional
    @Test
    void canCreateUserAccount() throws Exception {

        NewUserRequest body = new NewUserRequest("ena@foo.com", Role.DISPOSERS, null);
        mvc.perform(
                        post("/v1/users/register")
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
    }

    @Test
    @Transactional
    void createUserProfile() throws Exception {
        NewUserRequest body = new NewUserRequest("ena@foo.com", Role.DISPOSERS, null);
        mvc.perform(
                        post("/v1/users/register")
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        ProfileRequest profileRequest =
                new ProfileRequest(
                        "Elena",
                        "Xe",
                        LocalDate.of(1987, 2, 17),
                        new ProfileRequest.LocationInfo(
                                "221B", "Baker Street", "Oxford", "London", "12333", "UK"));
        mvc.perform(
                        post("/v1/profiles")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(profileRequest))
                                .with(jwt().jwt(j -> j.subject("ena@foo.com"))))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @Transactional
    void createDisposalRequest() throws Exception {
        NewUserRequest body = new NewUserRequest("ena@foo.com", Role.DISPOSERS, null);
        mvc.perform(
            post("/v1/users/register")
              .content(objectMapper.writeValueAsString(body))
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().is(200));

        ProfileRequest profileRequest =
          new ProfileRequest(
            "Elena",
            "Xe",
            LocalDate.of(1987, 2, 17),
            new ProfileRequest.LocationInfo(
              "221B", "Baker Street", "Oxford", "London", "12333", "UK"));
        mvc.perform(
            post("/v1/profiles")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(profileRequest))
              .with(jwt().jwt(j -> j.subject("ena@foo.com"))))
          .andExpect(status().is2xxSuccessful());

        Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_DISPOSERS"));

        DisposalRequest request = new DisposalRequest(Label.MSW, 91, Priority.HIGH);
        mvc.perform(
                        post("/v1/disposals")
                                .with(jwt().jwt(j -> j.subject("ena@foo.com").claim("authorities", authorities)).authorities(authorities))
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }
}
