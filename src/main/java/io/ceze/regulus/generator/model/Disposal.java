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

import io.ceze.regulus.commons.BaseEntity;
import io.ceze.regulus.generator.service.DisposalStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "disposals")
public class Disposal extends BaseEntity {
  private Long id;

  @Enumerated(EnumType.STRING)
  private Label label;

  private DisposalStatus status;
  private DisposalInfo disposalInfo;

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
}
