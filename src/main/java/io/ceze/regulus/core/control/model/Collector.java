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
package io.ceze.regulus.core.control.model;

import io.ceze.regulus.commons.data.BaseEntity;
import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.security.User;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "collectors")
public class Collector extends BaseEntity {

    @Id
    @Column(name = "collector_id")
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "available")
    private boolean available;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "collector")
    private Set<CollectorAgent> collectorAgents;

    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    public Collector() {}

    public Collector(String name, boolean available, User user) {
        this.name = name;
        this.available = available;
        this.user = user;
    }

    public Collector(String name) {
        this.name = name;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
