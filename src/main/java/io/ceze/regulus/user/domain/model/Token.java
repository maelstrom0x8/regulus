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
package io.ceze.regulus.user.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Token
{

	private User user;
	private String value;
	private LocalDateTime expiresAt;

	private Token()
	{
	}

	public Token(User user, String value, Duration ttl)
	{
		this.user = user;
		this.value = value;
		expiresAt = LocalDateTime.now().plus(ttl);
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getValue()
	{
		return value;
	}

	public boolean isExpired()
	{
		return LocalDateTime.now().isAfter(expiresAt);
	}

	public LocalDateTime expiresAt()
	{
		return expiresAt;
	}
}
