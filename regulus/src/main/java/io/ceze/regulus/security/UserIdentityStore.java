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
package io.ceze.regulus.security;

import static jakarta.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;

import io.ceze.regulus.account.service.AccountService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.Set;

@ApplicationScoped
public class UserIdentityStore implements IdentityStore {

  @Inject AccountService accountService;

  public CredentialValidationResult validate(UsernamePasswordCredential credential) {

    User user = accountService.loadByUsername(credential.getCaller());
    if (user != null && credential.compareTo(user.getUsername(), user.getPassword())) {
      return new CredentialValidationResult(user.getUsername(), Set.of("user"));
    }

    return INVALID_RESULT;
  }
}
