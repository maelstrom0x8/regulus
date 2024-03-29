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
package io.ceze.regulus.commons.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public abstract class JpaRepository<T, U> implements Repository<T, U> {

  protected final Class<T> entityClass;

  @PersistenceContext(name = "myPU", type = PersistenceContextType.TRANSACTION)
  protected EntityManager em;

  protected JpaRepository(Class<T> clss) {
    this.entityClass = clss;
  }

  @Override
  public T save(T t) {
    em.persist(t);
    return t;
  }

  @Override
  public List<T> saveAll(Iterable<T> iterable) {
    iterable.forEach(em::persist);
    return (List<T>) iterable;
  }

  @Override
  public Optional<T> findById(U id) {
    return Optional.ofNullable(em.find(entityClass, id));
  }

  @Override
  public Iterable<T> findAll() {
    CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(entityClass);
    query.select(query.from(entityClass));
    return em.createQuery(query).getResultList();
  }

  @Override
  public void deleteAll() {
    CriteriaDelete<T> delete = em.getCriteriaBuilder().createCriteriaDelete(entityClass);
    delete.from(entityClass);
    em.createQuery(delete).executeUpdate();
  }

  @Override
  public void deleteById(U id) {
    T entity = em.find(entityClass, id);
    if (entity != null) {
      em.remove(entity);
    }
  }

  public void update(T t) {
    em.merge(t);
  }

  public void setEntityManager(EntityManager em) {
    this.em = em;
  }
}
