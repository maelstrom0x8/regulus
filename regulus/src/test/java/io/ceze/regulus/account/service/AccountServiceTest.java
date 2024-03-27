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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.ceze.regulus.account.repository.AccountRepository;
import io.ceze.regulus.account.web.AccountRequest;
import io.ceze.regulus.account.web.AccountResponse;
import io.ceze.regulus.security.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.ceze.regulus.account.web.AccountType;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @InjectMocks private AccountService accountService;

  @Mock AccountRepository userRepository;

  @BeforeAll
  static void setUp() {}

  @Test
  void registerAccount() {
    AccountRequest accountRequest = new AccountRequest("alan", "secret", "alan@regulus.com", AccountType.BASIC);
    User user =
        new User(accountRequest.username(), accountRequest.password(), accountRequest.email());
    Mockito.when(userRepository.save(user)).thenReturn(user);

    AccountResponse accountResponse = accountService.registerAccount(accountRequest);

    assertEquals(accountResponse.username(), user.getUsername());
  }

  @Test
  void updatePassword() {}

  @Test
  void deleteAccount() {}

  @Test
  void loadByUsername() {}
}
