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

import io.ceze.regulus.account.web.LocationData;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
public class Location implements Serializable {

  @Id
  @Column(name = "location_id")
  @GeneratedValue
  private Long id;

  private int number;
  private String street;
  private String city;
  private String state;
  private String country;
  private String zipCode;

  @Column(name = "registered_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime registeredAt;

  public static Location from(LocationData location) {
    return new Location();
  }

  public static LocationData from(Location location) {
    return new LocationData(location.getCountry(), location.getState(), location.getCity());
  }

  @PrePersist
  public void onPersist() {
    registeredAt = LocalDateTime.now();
  }

  public Location() {}

  public Location(
      int number, String street, String city, String state, String country, String zipCode) {
    this.number = number;
    this.street = street;
    this.city = city;
    this.state = state;
    this.country = country;
    this.zipCode = zipCode;
  }

  public Long getId() {
    return id;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public LocalDateTime getRegisteredAt() {
    return registeredAt;
  }
}
