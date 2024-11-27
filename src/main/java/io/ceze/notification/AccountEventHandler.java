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
package io.ceze.notification;

import io.ceze.regulus.event.UserCreated;
import io.ceze.regulus.user.domain.service.token.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AccountEventHandler
{

	private static final Logger log = LoggerFactory.getLogger(AccountEventHandler.class);

	private final TokenManager tokenManager;
	private final MailService mailService;

	public AccountEventHandler(TokenManager tokenManager, MailService mailService)
	{
		this.tokenManager = tokenManager;
		this.mailService = mailService;
	}

	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@TransactionalEventListener
	public void sendVerificationToken(UserCreated user)
	{
		log.info("Preparing account for user with id{}", user.user().getId());
		var token = tokenManager.generateToken(user.user());
		//		@formatter:off
        Message message =
                Message.MessageBuilder.withSender("admin@regulus.com")
                        .recipient(user.user().getEmail())
                        .content(
                                String.format(
                                        """
											Click the token to verify your account.
												http://regulus.com/accounts/verify?token=%s
											Token will expire in one minute.
											""",
                                        token.getValue()))
                        .build();
        //		 @formatter:on
		mailService.send(message);
	}
}
