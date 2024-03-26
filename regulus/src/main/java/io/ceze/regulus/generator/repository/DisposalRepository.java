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
package io.ceze.regulus.generator.repository;

import io.ceze.regulus.commons.data.JpaRepository;
import io.ceze.regulus.generator.model.Disposal;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@ApplicationScoped
public class DisposalRepository extends JpaRepository<Disposal, Long> {
  protected DisposalRepository() {
    super(Disposal.class);
  }

  public Disposal findByLocationId(Long locationId) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Disposal> query = cb.createQuery(Disposal.class);
    Root<Disposal> root = query.from(Disposal.class);

    query.select(root).where(cb.equal(root.get("location").get("id"), locationId));

    return em.createQuery(query).getSingleResult();
  }
}
