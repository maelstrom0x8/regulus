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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.ceze.regulus.generator.model.Disposal;
import io.ceze.regulus.generator.model.Label;
import io.ceze.regulus.generator.repository.DisposalRepository;
import io.ceze.regulus.generator.web.DisposalRequest;
import io.ceze.regulus.generator.web.Priority;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DisposalServiceTest {

  @InjectMocks DisposalServiceImpl service;

  @Mock DisposalRepository disposalRepository;

  @Test
  void newDisposalRequest() {
    DisposalResponse disposalResponse = new DisposalResponse(24L, DisposalStatus.PENDING);
    Disposal disposal = new Disposal();
    disposal.setStatus(DisposalStatus.PENDING);

    when(disposalRepository.save(disposal)).thenReturn(disposal);

    DisposalResponse response =
        service.newDisposalRequest(new DisposalRequest(Label.MSW, 30, Priority.LOW));

    assertNotNull(response);
    assertEquals(DisposalStatus.PENDING, response.status());
  }

  @Test
  void getDisposalStatusForNonExistingDisposal() {
    when(disposalRepository.findById(23L)).thenReturn(Optional.empty());

    DisposalStatus status = service.getDisposalStatus(23L);

    assertNull(status);
  }
}
