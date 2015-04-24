// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a manufactured reusable thing that is used in healthcare.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Device")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_DEVICE_ID)
public class Device {
  /** External identifier from manufacturer, owner and others. */
  @JsonProperty("@id")
  private Id<Device> id;

  /** What kind of device this is. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Status of the Device. */
  @JsonProperty("status")
  private ContactPartyType status;

  /** Name of device manufacturer. */
  @JsonProperty("manufacturer")
  private String manufacturer;

  /** Model id assigned by manufacturer. */
  @JsonProperty("model")
  private String model;

  /** Version number (i.e. software). */
  @JsonProperty("version")
  private String version;

  /** Date/Time the device was manufactured. */
  @JsonProperty("manufactureDate") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant manufactureDate;

  /** Date/Time of expiry of this device (if applicable). */
  @JsonProperty("expiry") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant expiry;

  /** FDA Mandated Unique Device Identifier. */
  @JsonProperty("udi")
  private String udi;

  /** Lot number of manufacture. */
  @JsonProperty("lotNumber")
  private String lotNumber;

  /** Organization responsible for this device. */
  @JsonProperty("ownerId")
  private Id<Organization> ownerId;

  /** Organization responsible for this device. */
  @JsonProperty("location")
  private Location location;

  /** If the device is affixed to a person. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Details for human/organization for support. */
  @JsonProperty("contact")
  private ContactPoint contact;

  /** URL to connect device. */
  @JsonProperty("url")
  private URI url;
}
