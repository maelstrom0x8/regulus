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
package io.ceze.regulus.generator.model;

import io.ceze.regulus.commons.data.BaseEntity;
import io.ceze.regulus.commons.data.Location;
import io.ceze.regulus.generator.service.DisposalStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "disposals")
public class Disposal extends BaseEntity {

    @Id
    @Column(name = "disposal_id")
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "label")
    private Label label;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DisposalStatus status;

    private DisposalInfo disposalInfo;

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
}
