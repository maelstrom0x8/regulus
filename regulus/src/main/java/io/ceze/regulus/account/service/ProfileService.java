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
import io.ceze.regulus.account.web.ProfileDataRequest;
import io.ceze.regulus.account.web.ProfileDataResponse;
import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.security.User;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@ApplicationScoped
public class ProfileService {

  @Inject private AccountRepository accountRepository;

  @Inject SecurityContext securityContext;

  private User user;

  @PostConstruct
  void loadUser() {
    String name = securityContext.getCallerPrincipal().getName();
    this.user = accountRepository.findByUsername(name).orElseThrow();
  }

  public ProfileDataResponse updateProfile(String user, ProfileDataRequest request) {
    Optional<User> optional = accountRepository.findByUsername(user);
    if (optional.isPresent()) {
      this.user.setLocation(Location.from(request.location()));
      this.user.setEmail(request.email());
      this.user.setUsername(request.username());
    } else {
      throw new RuntimeException("Can only update your profile");
    }

    return from(accountRepository.save(this.user));
  }

  private ProfileDataResponse from(@NotNull User user) {
    return new ProfileDataResponse(
        user.getUsername(),
        user.getFirstName(),
        user.getLastName(),
        Location.from(user.getLocation()));
  }

  public ProfileDataResponse getProfile() {
    return from(user);
  }
}
