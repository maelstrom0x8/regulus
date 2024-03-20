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
package io.ceze.regulus.user;

import io.ceze.regulus.commons.JpaRepository;
import io.ceze.regulus.security.User;
import java.util.Optional;

public abstract class UserRepository extends JpaRepository<User, Long> {

  public UserRepository() {
    super(User.class);
  }

  public Optional<User> findByUsername(String username) {
    return Optional.empty();
  }
}
