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
package io.ceze.regulus.core.control.service;

import io.ceze.regulus.core.control.service.cluster.ClusterManager;
import io.ceze.regulus.core.generator.model.Disposal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CollectionService {

    private static final Logger log = LoggerFactory.getLogger(CollectionService.class);
    private final ClusterManager clusterManager;

    public CollectionService(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    @Async
    @TransactionalEventListener
    public void onDisposalRequestInitiated(Disposal disposal) {
        log.info("Processing disposal request");
        handleDisposal(disposal);
    }

    private void handleDisposal(Disposal disposal) {
        clusterManager.add(disposal);
        /*
         * TODO: Support notification for users in close proximity
         *  with the disposal request
         */
    }
}
