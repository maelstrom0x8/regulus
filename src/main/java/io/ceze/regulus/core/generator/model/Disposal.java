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
package io.ceze.regulus.core.generator.model;

import io.ceze.regulus.core.generator.service.DisposalStatus;
import io.ceze.regulus.user.domain.model.Location;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "disposals")
public class Disposal {

    @Id
    @Column(name = "disposal_id", columnDefinition = "bigserial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "lbl", columnDefinition = "label")
    private Label label;

    @Column(name = "status", columnDefinition = "disposal_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    private DisposalStatus status;

    private DisposalInfo disposalInfo;

    @CreationTimestamp private LocalDateTime initiatedAt;

    @UpdateTimestamp private LocalDateTime lastModified;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    public Disposal() {}

    public Disposal(Label label, DisposalStatus status, DisposalInfo disposerInfo) {
        this.label = label;
        this.status = status;
        this.disposalInfo = disposerInfo;
    }

    public Long getId() {
        return id;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public DisposalStatus getStatus() {
        return status;
    }

    public void setStatus(DisposalStatus status) {
        this.status = status;
    }

    public DisposalInfo getDisposalInfo() {
        return disposalInfo;
    }

    public void setDisposalInfo(DisposalInfo disposalInfo) {
        this.disposalInfo = disposalInfo;
    }

    public void setId(long l) {
        this.id = l;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public LocalDateTime getInitiatedAt() {
        return initiatedAt;
    }
}
