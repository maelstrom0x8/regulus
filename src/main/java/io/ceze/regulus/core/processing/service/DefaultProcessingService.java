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

package io.ceze.regulus.core.processing.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ceze.gis.LocationService;
import io.ceze.regulus.core.generator.payload.model.Label;
import io.ceze.regulus.core.processing.model.Operable;
import io.ceze.regulus.core.processing.model.Operator;
import io.ceze.regulus.core.processing.model.PayloadType;
import io.ceze.regulus.core.processing.repository.OperatorRepository;
import io.ceze.regulus.event.OperatorCreated;
import io.ceze.regulus.user.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
class DefaultProcessingService implements ProcessingService
{
	private static final Logger log = LoggerFactory.getLogger(DefaultProcessingService.class);
	private final OperatorRepository operatorRepository;
	private final LocationService locationService;
	private final ObjectMapper objectMapper;


	DefaultProcessingService (OperatorRepository operatorRepository, LocationService locationService, ObjectMapper objectMapper)
	{
		this.operatorRepository = operatorRepository;
		this.locationService = locationService;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public void addOperator (Operator operator)
	{
		operatorRepository.save(operator);
	}

	@Override
	public List<Operable> retrieveOperatorsByLabel (Label label)
	{
		return List.of();
	}
	

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void newOperator (OperatorCreated op) throws Exception
	{
		String s = (String) op.properties().get("type");
		Operator operator = null;
		switch (s)
			{
				case "collector":
					operator = addCollectorOperator(op.user(), op.properties());
					break;
				case "recycling":
					addRecyclingOperator(op.user(), op.properties());
					break;
				default:
					throw new Exception("Unknown operator type");
			}
			log.info("Operator created with id {}", operator.getId());
	}

	@Transactional
	private Operator addCollectorOperator (User user, Map<String, Object> properties) throws Exception
	{
		Collection<String> payloadTypes = getProperty(properties, "payloadType",
			new TypeReference<>() {});
		Integer capacity = getProperty(properties, "capacity", Integer.class);
		String name = getProperty(properties, "name", String.class);
		Set<PayloadType> types = payloadTypes.stream().map(PayloadType::valueOf).collect(Collectors.toSet());
		Operator operator = new Operator(name, user, capacity);
		operator.getPayloadTypes().addAll(types);
		operatorRepository.save(operator);
		log.info("New operator with name: {}", name);
		return operator;
	}

	private void addRecyclingOperator (User user, Map<String, Object> properties)
	{

	}

	private <T> T getProperty (Map<String, Object> properties, String name, TypeReference<T> typeReference) throws Exception
	{
		Object value = properties.get(name);

		if (value == null)
		{
			throw new PropertyNotFoundException();
		}

		return objectMapper.convertValue(value, typeReference);
	}

	private <T> T getProperty (Map<String, Object> properties, String name, Class<T> valueType) throws Exception
	{
		Object value = properties.get(name);

		if (value == null)
		{
			throw new PropertyNotFoundException();
		}

		return objectMapper.convertValue(value, valueType);
	}
}
