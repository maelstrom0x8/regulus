/*
 * Copyright (C) 2024 Emmanuel Godwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"{}
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
package io.ceze.regulus.core.collector.web;

import io.ceze.config.security.Authenticated;
import io.ceze.regulus.core.collector.model.AgentId;
import io.ceze.regulus.core.collector.model.Collector;
import io.ceze.regulus.core.collector.model.CollectorAgent;
import io.ceze.regulus.core.collector.service.CollectorRegistration;
import io.ceze.regulus.core.collector.service.CollectorService;
import io.ceze.regulus.user.domain.ContactDetails;
import io.ceze.regulus.user.domain.model.projection.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/collectors")
@PreAuthorize("hasRole('COLLECTOR')")
public class CollectorController
{

	private static final String AGENT_ID = "agent_id";
	private static final String COLLECTOR_ID = "collector_id";

	private final CollectorService collectorService;

	public CollectorController(CollectorService collectorService)
	{
		this.collectorService = collectorService;
	}

	@GetMapping("/agents")
	public List<CollectorAgentResponse> getCollectorAgents(@Authenticated UserId collectorId,
																												 @RequestParam("page") int page,
																												 @RequestParam("size") int size)
	{
		return collectorService.getCollectorAgentsByCollectorId(collectorId.id())
			.stream().map(CollectorAgentResponse::from)
			.toList();
	}

	@GetMapping("/agents/{agent_id}")
	public CollectorAgentResponse fetchCollectorAgentById(@Authenticated UserId collectorId,
																											@PathVariable(AGENT_ID) Long agentId)
	{
		CollectorAgent collectorAgent = collectorService.getCollectorAgentById(new AgentId(collectorId.id(), agentId));
		return CollectorAgentResponse.from(collectorAgent);
	}

	@PostMapping("/agents")
	public void createCollectorAgent(@Authenticated UserId collectorId,
																										 @RequestBody CollectorAgentRequest request)
	{
		collectorService.addCollectorAgent(collectorId.id(), request);
	}

	@PutMapping("/agents/{agent_id}")
	public CollectorAgentResponse updateCollectorAgent(@PathVariable(AGENT_ID) Long agentId,
																										 @RequestBody CollectorAgentRequest request)
	{
		return null;
	}

	@DeleteMapping("/agents/{agent_id}")
	public void deleteCollectorAgent(@Authenticated UserId userId, @PathVariable(AGENT_ID) Long agentId)
	{
		AgentId agent = new AgentId(userId.id(), agentId);
		collectorService.removeCollectorAgent(agent);
	}

	@GetMapping("/{collector_id}")
	public CollectorResponse fetchCollectorById(@Authenticated UserId userId, @PathVariable(COLLECTOR_ID) Long collectorId)
	{
		Collector collector = collectorService.getCollectorById(collectorId);
		return CollectorResponse.from(collector);
	}

	@PutMapping("/{collector_id}")
	public ResponseEntity<Void> putCollectorInfo(@Authenticated UserId userId, @PathVariable(COLLECTOR_ID) Long collectorId, @RequestBody CollectorRequest request) throws IllegalAccessException
	{
		if (!userId.id().equals(collectorId))
			throw new IllegalAccessException("Cannot update this data");

		var details = ContactDetails.of(request.name(), userId.email())
			.telephone(request.telephone()).countryCode(request.countryCode()).build();

		var registration = new CollectorRegistration(details);

		collectorService.updateCollectorInfo(registration);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{collector_id}")
	public void deleteCollector(@Authenticated UserId userId, @PathVariable(COLLECTOR_ID) Long collectorId) throws IllegalAccessException
	{
		if(!userId.id().equals(collectorId))
			throw new IllegalAccessException();
		collectorService.removeCollectorById(collectorId);
	}
}
