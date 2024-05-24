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
package io.ceze.regulus.core.control.repository;

import io.ceze.regulus.commons.data.JpaRepository;
import io.ceze.regulus.core.control.model.Collector;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

@ApplicationScoped
public class CollectorRepository extends JpaRepository<Collector, Long> {

    public CollectorRepository() {
        super(Collector.class);
    }

    public List<Collector> findAllByCity(String city) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Collector> query = builder.createQuery(Collector.class);
        Root<Collector> root = query.from(Collector.class);
        query.select(root).where(builder.equal(root.get("location").get("city"), city));
        TypedQuery<Collector> typedQuery = em.createQuery(query);
        return typedQuery.getResultList();
    }
}
