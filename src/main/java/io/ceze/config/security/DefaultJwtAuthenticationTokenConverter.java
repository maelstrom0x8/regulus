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

import io.ceze.regulus.user.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public class DefaultJwtAuthenticationTokenConverter
	implements Converter<Jwt, CustomJwtAuthenticationToken>
{

	@Autowired
	private UserService userService;

	@Override
	@SuppressWarnings("unchecked")
	public CustomJwtAuthenticationToken convert(Jwt source)
	{
		List<String> authorities = (List<String>) source.getClaims().get("authorities");

		return new CustomJwtAuthenticationToken(
			source, authorities.stream().map(SimpleGrantedAuthority::new).toList());
	}
}
