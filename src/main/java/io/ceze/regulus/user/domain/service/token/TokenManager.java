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
package io.ceze.regulus.user.domain.service.token;

import io.ceze.notification.MailService;
import io.ceze.regulus.user.domain.model.Token;
import io.ceze.regulus.user.domain.model.User;
import io.ceze.regulus.user.domain.repository.TokenStore;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Random;

@Component
public class TokenManager
{
	private static final Logger log = LoggerFactory.getLogger(TokenManager.class);

	private final ApplicationEventPublisher eventPublisher;
	private final EntityManager em;
	private final MailService mailService;
	private final TokenStore tokenStore;

	public TokenManager(
		EntityManager em,
		ApplicationEventPublisher eventPublisher,
		MailService mailService,
		TokenStore tokenStore)
	{
		this.em = em;
		this.eventPublisher = eventPublisher;
		this.mailService = mailService;
		this.tokenStore = tokenStore;
	}

	public Token generateToken(User user)
	{
		String tokenValue = generateSecureToken(20, 32);
		Token token = new Token(user, tokenValue, Duration.ofMinutes(1L));
		final Long id = user.getId();
		log.info(
			"Generated token {} for user with id {}. Expires at {}",
			tokenValue,
			id,
			token.expiresAt());
		tokenStore.put(id, token);
		return token;
	}

	private String generateSecureToken(int lowerBound, int upperBound)
	{
		int length = new Random().nextInt(lowerBound, upperBound);
		SecureRandom secureRandom = new SecureRandom();
		byte[] randomBytes = new byte[length];
		secureRandom.nextBytes(randomBytes);

		return Base64.getUrlEncoder()
			.withoutPadding()
			.encodeToString(randomBytes)
			.substring(0, length);
	}

	public void verify(TokenVerification tokenVerification) throws ExpiredTokenException
	{
		try
		{
			Token token =
				tokenStore
					.get(tokenVerification.userId().id())
					.orElseThrow(InvalidTokenException::new);

			log.info("Found token for user {}", token.getUser().getId());

			if (token.isExpired())
			{
				log.error("Token is expired");
				throw new ExpiredTokenException();
			}

			if (!token.getValue().equals(tokenVerification.token()))
			{
				log.error(
					"Token contents mismatch t1={}, t2={}",
					token.getValue(),
					tokenVerification.token());
				throw new InvalidTokenException("Problem with token contents");
			} else
			{
				token.getUser().setActive(true);
				token.getUser().setVerified(true);
				em.merge(token.getUser());
			}

		} catch (IllegalArgumentException | NullPointerException e)
		{
			throw new IllegalArgumentException("Invalid token or format", e);
		}
	}
}
