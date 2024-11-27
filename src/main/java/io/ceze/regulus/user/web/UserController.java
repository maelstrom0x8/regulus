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
package io.ceze.regulus.user.web;

import io.ceze.config.security.Authenticated;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.service.UserService;
import io.ceze.regulus.user.domain.service.token.ExpiredTokenException;
import io.ceze.regulus.user.dto.NewUserRequest;
import io.ceze.regulus.user.dto.ProfileRequest;
import io.ceze.regulus.user.dto.ProfileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
public class UserController
{

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private final UserService userService;

	public UserController(UserService userService)
	{
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<Void> registerUser(@RequestBody NewUserRequest userRequest)
	{
		userService.create(userRequest);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify")
	public void verifyToken(@Authenticated UserId userId, @RequestParam("token") String token)
		throws ExpiredTokenException
	{
		log.info("Verifying token");
		userService.verifyUser(userId, token);
		log.info("Verified user with id {}", userId.id());
	}

	@PostMapping("/resend-verification-token")
	public void tokenResend(@Authenticated UserId userId)
	{
		log.info("Requesting new verification token");
		userService.resendToken(userId);
	}

	@PostMapping("/profile/{profile_id}")
	public ProfileResponse updateProfile(
		@Authenticated UserId userId,
		@PathVariable("profile_id") Long profileId,
		ProfileRequest request)
	{
		return null;
	}

	@DeleteMapping
	public void deleteAccount(@Authenticated UserId userId)
	{
		userService.deleteAccount(userId);
	}
}
