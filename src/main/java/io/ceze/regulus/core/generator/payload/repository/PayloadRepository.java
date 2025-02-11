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
package io.ceze.regulus.core.generator.payload.repository;

import io.ceze.regulus.core.generator.payload.model.Payload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PayloadRepository extends JpaRepository<Payload, Long>
{

	@Query("SELECT s FROM Payload s WHERE s.location.id = :id")
	Optional<Payload> findByLocationId(@Param("id") Long locationId);

	@Query(
		nativeQuery = true,
		value = "SELECT * FROM payloads p WHERE p.payload_id = :id")
	Optional<Payload> findByPayloadId(@Param("id") Long payloadId);
}
