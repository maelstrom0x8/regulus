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
package io.ceze.regulus.account.service;

import io.ceze.regulus.account.repository.AccountRepository;
import io.ceze.regulus.account.web.AccountRequest;
import io.ceze.regulus.account.web.AccountResponse;
import io.ceze.regulus.commons.data.LocationRepository;
import io.ceze.regulus.commons.exception.UserNotFoundException;
import io.ceze.regulus.security.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import org.jboss.logging.Logger;

import java.security.Principal;
import javax.security.auth.login.AccountNotFoundException;


@ApplicationScoped
public class AccountService {

  private static final Logger LOG = Logger.getLogger(AccountService.class);

  @Inject private AccountRepository userRepository;
  @Inject private LocationRepository locationRepository;

  @Inject SecurityContext securityContext;

  public AccountResponse registerAccount(AccountRequest accountRequest) {
    LOG.infof("Register new user {}", accountRequest);
    User user =
        new User(accountRequest.username(), accountRequest.password(), accountRequest.email());

    User saved = userRepository.save(user);
    LOG.infof("User registered successfully {}", saved.getId());
    return new AccountResponse(
        saved.getUsername(), saved.getEmail(), AccountResponse.AccountStatus.CREATED);
  }

  public void updatePassword(String newPassword) throws AccountNotFoundException {
    Principal principal = securityContext.getCallerPrincipal();
    if (principal == null) return;
    User user =
        userRepository
            .findByUsername(principal.getName())
            .orElseThrow(AccountNotFoundException::new);

    user.setPassword(newPassword);
    userRepository.save(user);
  }

  public void deleteAccount() throws AccountNotFoundException {
    String name = securityContext.getCallerPrincipal().getName();
    User user = userRepository.findByUsername(name).orElseThrow(AccountNotFoundException::new);

    userRepository.deleteById(user.getId());
  }

  public User loadByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("User does not exist"));
  }
}
