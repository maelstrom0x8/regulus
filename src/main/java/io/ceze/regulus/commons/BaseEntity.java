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
package io.ceze.regulus.commons;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity implements Serializable {

  @ManyToOne(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
  @JoinColumn(name = "location_id")
  protected Location location;

  protected LocalDateTime createdAt;

  protected LocalDateTime lastModified;

  @PrePersist
  public void onPersist() {
    this.createdAt = LocalDateTime.now();
    this.lastModified = LocalDateTime.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.lastModified = LocalDateTime.now();
  }

  public BaseEntity() {}

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }
}
