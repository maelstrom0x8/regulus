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
package io.ceze.config.security;

import io.ceze.config.web.resolvers.AuthenticatedUserResolver;
import io.ceze.regulus.user.domain.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration implements WebMvcConfigurer
{

	private static final String[] ALLOWED_ENDPOINTS = {"/actuator/health", "/v1/users/register", "/v1/profiles/inf"};
	private final AuthenticationService authenticationService;
	private final UserService userService;

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
	private String jwkSetUri;

	public WebSecurityConfiguration(
		AuthenticationService authenticationService, UserService userService)
	{
		this.authenticationService = authenticationService;
		this.userService = userService;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers)
	{
		resolvers.add(new AuthenticatedUserResolver(authenticationService, userService));
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
	{

		http.csrf(CsrfConfigurer::disable).cors(CorsConfigurer::disable);
		http.authorizeHttpRequests(
			requests -> requests.requestMatchers(ALLOWED_ENDPOINTS).permitAll());
		http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated());

		http.oauth2ResourceServer(
			server ->
				server.jwt(
					jwt ->
						jwt.jwtAuthenticationConverter(
							new DefaultJwtAuthenticationTokenConverter())));

		return http.build();
	}


	@Bean
	public JwtDecoder jwtDecoder ()
	{
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	}
}
