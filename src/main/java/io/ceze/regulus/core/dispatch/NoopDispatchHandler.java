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
package io.ceze.regulus.core.dispatch;

import io.ceze.regulus.core.cluster.Cluster;
import io.ceze.regulus.core.generator.payload.model.PayloadStatus;
import io.ceze.regulus.event.CollectionEvent;
import io.ceze.regulus.event.CollectionEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * A NoopDispatchHandler class that implements the DispatchHandler interface.
 * This handler is a no-operation implementation used for dispatching operations
 * within a cluster.
 */
@Profile(value = {"default", "test"})
@Component
class NoopDispatchHandler implements DispatchHandler
{

	private static final Logger LOG = LoggerFactory.getLogger(NoopDispatchHandler.class);

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	public NoopDispatchHandler()
	{
		LOG.warn("Using {noop} dispatch handler");
	}

	@Override
	public boolean dispatch(Cluster cluster)
	{
		LOG.info("Dispatch agents candidate cluster {}", cluster.getRequestQueue());
		cluster.getRequestQueue()
			.forEach(
				e ->
				{
					e.setStatus(PayloadStatus.PROCESSED);
					eventPublisher.publishEvent(
						new CollectionEvent(CollectionEventType.PROCESSED, e));
				});
		return true;
	}
}
