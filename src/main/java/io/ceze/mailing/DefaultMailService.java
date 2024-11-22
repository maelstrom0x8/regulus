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
package io.ceze.mailing;

import io.ceze.regulus.event.AccountVerification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
class DefaultMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(DefaultMailService.class);

    private final JavaMailSender mailSender;

    DefaultMailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @TransactionalEventListener
    public void accountVerification(AccountVerification verification) {
        String email = verification.userId().email();
        String token = verification.token();
        Message message =
                Message.MessageBuilder.withSender("admin@regulus.com")
                        .recipient(email)
                        .content(
                                String.format(
                                        """
                        Click the token to verify your account.
                        http://regulus.com/accounts/verification?token=%s

                        Token will expire in one minute.
                        """,
                                        token))
                        .build();
        send(message);
    }

    @Override
    public void send(Message message) throws MailException {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(message.getSender());
        String[] recipients =
                message.getRecipients().toArray(new String[message.getRecipients().size()]);
        mailMessage.setTo(recipients);
        mailMessage.setText(message.getContent());

        log.info("Sending message to {}", message.getRecipients());
        mailSender.send(mailMessage);
    }
}
