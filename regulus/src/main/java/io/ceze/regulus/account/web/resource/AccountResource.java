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
package io.ceze.regulus.account.web.resource;

import io.ceze.regulus.account.service.AccountService;
import io.ceze.regulus.account.web.AccountRequest;
import io.ceze.regulus.account.web.AccountResponse;
import io.ceze.regulus.security.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("v1/accounts")
@RequestScoped
public class AccountResource {

  private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

  @Inject private AccountService accountService;

  @POST
  @Path("/register")
  public AccountResponse createAccount(AccountRequest accountRequest) {
    return accountService.registerAccount(accountRequest);
  }

  @GET
  public List<User> fetchAllUsers() {
    return Collections.emptyList();
  }

  public void deactivate() {}
}
