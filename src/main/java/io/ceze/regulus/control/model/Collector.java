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
package io.ceze.regulus.control.model;

import io.ceze.regulus.commons.BaseEntity;
import io.ceze.regulus.commons.Location;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "collectors")
public class Collector extends BaseEntity {

  @Id
  @Column(name = "collector_id")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "collector_id_seq")
  @SequenceGenerator(name = "collector_id_seq", allocationSize = 1, initialValue = 1001)
  private Long id;

  private String name;
  private boolean available;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "collector")
  private Set<CollectorAgent> collectorAgents;

  public Collector(String name, boolean available, Location location) {
    this.name = name;
    this.available = available;
    this.location = location;
  }

  public Collector(String name, Location location) {
    this.name = name;
    this.location = location;
    this.available = false;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean available) {
    this.available = available;
  }

  public Set<CollectorAgent> getCollectorAgents() {
    return collectorAgents;
  }

  public void setCollectorAgents(Set<CollectorAgent> collectorAgents) {
    this.collectorAgents = collectorAgents;
  }
}
