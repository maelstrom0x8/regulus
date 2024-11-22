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

import io.ceze.regulus.event.AccountVerification;
import io.ceze.regulus.user.domain.model.Token;
import io.ceze.regulus.user.domain.model.User;
import io.ceze.regulus.user.domain.model.projection.UserId;
import io.ceze.regulus.user.domain.repository.TokenStore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class TokenManager {

    private static final Logger log = LoggerFactory.getLogger(TokenManager.class);

    @PersistenceContext private final EntityManager em;
    private final ApplicationEventPublisher eventPublisher;
    private final TokenStore tokenStore;

    public TokenManager(
            EntityManager em, ApplicationEventPublisher eventPublisher, TokenStore tokenStore) {
        this.em = em;
        this.eventPublisher = eventPublisher;
        this.tokenStore = tokenStore;
    }

    public Token generateToken(User user) {
        String tokenValue = generateSecureToken(20, 32); // Token length between 20-32
        Token token = new Token(user, tokenValue, Duration.ofMinutes(1L));
        log.info(
                "Generated token {} for user with id {}. Expires at {}",
                tokenValue,
                user.getId(),
                token.expiresAt());
        eventPublisher.publishEvent(
                new AccountVerification(new UserId(user.getId(), user.getEmail()), tokenValue));
        tokenStore.put(user.getId(), token);
        return token;
    }

    private String generateSecureToken(int lowerBound, int upperBound) {
        int length = new Random().nextInt(lowerBound, upperBound);
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes)
                .substring(0, length);
    }

    public void verify(AccountVerification accountVerification) throws ExpiredTokenException {
        try {
            Token token1 =
                    tokenStore
                            .get(accountVerification.userId().id())
                            .orElseThrow(InvalidTokenException::new);

            log.info("Found token for user {}", token1.getUser().getId());

            if (token1.isExpired()) {
                log.error("Token is expired");
                throw new ExpiredTokenException();
            }

            if (!token1.getValue().equals(accountVerification.token())) {
                log.error(
                        "Token contents mismatch t1={}, t2={}",
                        token1.getValue(),
                        accountVerification.token());
                throw new InvalidTokenException("Problem with token contents");
            } else {
                token1.getUser().setActive(true);
                token1.getUser().setVerified(true);
                em.merge(token1.getUser());
            }

        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid token or format", e);
        }
    }
}
