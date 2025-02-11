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
package io.ceze.config.web.resolvers;

import io.ceze.config.security.Authenticated;
import io.ceze.config.security.AuthenticatedUser;
import io.ceze.config.security.AuthenticationService;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticatedUserResolver implements HandlerMethodArgumentResolver
{

	private static final Logger log = LoggerFactory.getLogger(AuthenticatedUserResolver.class);
	private final AuthenticationService authenticationService;
	private final UserService userService;

	public AuthenticatedUserResolver(
		AuthenticationService authenticationService, UserService userService)
	{
		this.authenticationService = authenticationService;
		this.userService = userService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter)
	{
		return parameter.hasParameterAnnotation(Authenticated.class)
			&& parameter.getParameterType().equals(UserId.class);
	}

	@Override
	public Object resolveArgument(
		MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory)
		throws Exception
	{
		AuthenticatedUser authenticated = authenticationService.authenticated();
		var user = userService.getUserByEmail(authenticated.subject());
		if (user == null)
		{
			log.warn("User account does not exist");
			HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
			if (response != null)
				response.setHeader("Location", webRequest.getContextPath() + "/v1/users/register");
			throw new BadCredentialsException("Account does not exist");
		}

		return user;
	}
}
