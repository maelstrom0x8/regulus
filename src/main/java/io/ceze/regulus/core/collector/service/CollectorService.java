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
 *
 */
package io.ceze.regulus.core.collector.service;

import io.ceze.regulus.core.cluster.ClusterManager;
import io.ceze.regulus.core.collector.model.AgentId;
import io.ceze.regulus.core.collector.model.Collector;
import io.ceze.regulus.core.collector.model.CollectorAgent;
import io.ceze.regulus.core.collector.repository.CollectorRepository;
import io.ceze.regulus.core.collector.web.CollectorAgentRequest;
import io.ceze.regulus.core.generator.payload.model.Payload;
import io.ceze.regulus.event.CollectionEvent;
import io.ceze.regulus.event.CollectionEventType;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;
import java.util.Set;

@Service
public class CollectorService
{
	private static final Logger log = LoggerFactory.getLogger(CollectorService.class);
	private final CollectorRepository collectorRepository;
	private final ClusterManager clusterManager;

	private final EntityManager em;

	public CollectorService(CollectorRepository collectorRepository, ClusterManager clusterManager, EntityManager em)
	{
		this.collectorRepository = collectorRepository;
		this.clusterManager = clusterManager;
		this.em = em;
	}

	public void addCollectorAgent(Long collectorId, CollectorAgentRequest request)
	{
		Collector collector = getCollectorById(collectorId);
		CollectorAgent agent = new CollectorAgent(collector);
		agent.setAvailable(request.available());
		collector.addAgent(agent);

		collectorRepository.save(collector);
	}

	public CollectorAgent getCollectorAgentById(AgentId agentId)
	{
		return null;
	}

	public Set<CollectorAgent> getCollectorAgentsByCollectorId(Long collectorId)
	{
		return Collections.emptySet();
	}

	public Collector getCollectorById(Long collectorId)
	{
		return collectorRepository.findById(collectorId).orElseThrow();
	}

	public void removeCollectorAgent(AgentId agentId)
	{
		Collector collector = collectorRepository.findById(agentId.collectorId()).orElseThrow();
		collector.removeAgent(agentId);
	}

	public void updateCollectorInfo(CollectorRegistration collectorRegistration) {

	}

	public void removeCollectorById(Long collectorId)
	{
		collectorRepository.deleteById(collectorId);
	}


	@Async
	@TransactionalEventListener
	public void onPayloadGenerated(Payload payload)
	{
		log.info("Processing payload request");
		process(payload);
	}

	private void process(Payload payload)
	{
		clusterManager.add(payload);
		/*
		 * TODO: Support notification for users in close proximity
		 *  with the payload request
		 */
	}

	@Async
	@TransactionalEventListener
	public void payloadCollectionHandler(CollectionEvent collectionEvent)
	{
		if(collectionEvent.type().equals(CollectionEventType.PROCESSED))
		{
			log.info("Payload processed: {}, status={}", collectionEvent.payload().getId(), collectionEvent.payload().getStatus());
			em.merge(collectionEvent.payload());
		}
	}
}
