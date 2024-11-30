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
package io.ceze.regulus.user.domain.service;

import io.ceze.regulus.core.generator.payload.model.Generator;
import io.ceze.regulus.event.OperatorCreated;
import io.ceze.regulus.event.UserCreated;
import io.ceze.regulus.user.domain.model.User;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.repository.UserRepository;
import io.ceze.regulus.user.domain.service.token.TokenManager;
import io.ceze.regulus.user.domain.service.token.TokenVerification;
import io.ceze.regulus.user.dto.NewUserRequest;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService
{

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final ApplicationEventPublisher eventPublisher;
	private final TokenManager tokenManager;
	private final UserRepository userRepository;

	public UserService(
		ApplicationEventPublisher eventPublisher,
		TokenManager tokenManager,
		UserRepository userRepository)
	{
		this.eventPublisher = eventPublisher;
		this.tokenManager = tokenManager;
		this.userRepository = userRepository;
	}

	@Transactional()
	public void create(NewUserRequest userRequest) throws DuplicateAccountException
	{
		if (userRepository.existsByEmail(userRequest.email()))
		{
			log.error("Duplicates not allowed. User already exists.");
			throw new DuplicateAccountException();
		}

		try
		{
			User user = User.withRole(userRequest.role());
			user.setEmail(userRequest.email());
			userRepository.save(user);
			if(!(user instanceof Generator))
				eventPublisher.publishEvent(new OperatorCreated(user, userRequest.properties()));
			log.info("User account for {} created successfully", user.getEmail());
			eventPublisher.publishEvent(new UserCreated(user));

		} catch (DuplicateAccountException e)
		{
			throw e;
		} catch (Exception e)
		{
			log.error("Unable to save user account. {}", e.getMessage());
		}
	}

	/// Deletes the user account and all attached resources.
	///
	/// @param userId The current authenticated user
	public void deleteAccount(UserId userId)
	{
		userRepository.deleteById(userId.id());
	}

	@Cacheable
	public UserId getUserByEmail(@NotNull String email)
	{
		log.info("Get user with email={}", email);
		return userRepository.findByEmail(email).orElseThrow(AccountNotFoundException::new);
	}

	@Cacheable
	public User getUserById(Long userId)
	{
		return userRepository.findById(userId).orElseThrow(AccountNotFoundException::new);
	}

	public void verifyUser(UserId userId, String token)
	{
		TokenVerification verification = new TokenVerification(userId, token);
		tokenManager.verify(verification);
	}

	public void resendToken(UserId userId)
	{
		User user = getUserById(userId.id());
		eventPublisher.publishEvent(new UserCreated(user));
	}

}
