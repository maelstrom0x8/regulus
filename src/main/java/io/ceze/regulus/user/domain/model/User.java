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
package io.ceze.regulus.user.domain.model;

import io.ceze.regulus.core.control.model.Collector;
import io.ceze.regulus.core.generator.model.Recycler;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public abstract class User {

    @Id
    @Column(name = "user_id")
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "users_id_seq",
            initialValue = 1000,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    protected Long id;

    @Column(nullable = false, unique = true, length = 254)
    protected String email;

    protected transient Role role;
    protected boolean verified;
    protected boolean active;

    @CreationTimestamp protected LocalDateTime createdAt;

    @UpdateTimestamp protected LocalDateTime lastModified;

    public User() {}

    public User(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public static User withRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        return switch (role) {
            case DISPOSERS -> new Disposer();
            case COLLECTORS -> new Collector();
            case RECYCLERS -> new Recycler();
        };
    }

    public Role getRole() {
        return determineRole();
    }

    private Role determineRole() {
        if (this instanceof Disposer) {
            return Role.DISPOSERS;
        } else if (this instanceof Collector) {
            return Role.COLLECTORS;
        } else if (this instanceof Recycler) {
            return Role.RECYCLERS;
        } else {
            throw new IllegalStateException("Unknown role for user");
        }
    }
}
