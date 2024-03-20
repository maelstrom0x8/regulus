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
package io.ceze.regulus.generator.service;

import io.ceze.regulus.control.service.CollectionService;
import io.ceze.regulus.user.UserServiceImpl;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DefaultWasteServiceTest {

  @Mock WasteService wasteService;

  @InjectMocks private UserServiceImpl userService;

  @InjectMocks private CollectionService collectionService;

  @Inject private SecurityContext securityContext;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
}
