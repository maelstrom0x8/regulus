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

import io.ceze.regulus.commons.exception.UserNotFoundException;
import io.ceze.regulus.generator.model.Disposal;
import io.ceze.regulus.generator.web.DisposalRequest;

public interface DisposalService {

    DisposalResponse newDisposalRequest(DisposalRequest request)
        throws LocationNotFoundException, UserNotFoundException;

    DisposalStatus getDisposalStatus(Long disposalId)
        throws LocationNotFoundException, UserNotFoundException;

    void update(Disposal disposal);
}
