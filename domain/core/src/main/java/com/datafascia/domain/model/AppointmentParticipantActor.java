// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Actor element in the Participant element in the Appointment model.
 * Represents a reference to one element of one of these types:
 * Patient | Practitioner | RelatedPerson | Device | HealthcareService | Location
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "AppointmentParticipantActor")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class AppointmentParticipantActor {
  /** Who/what is participating in the appointment. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Who/what is participating in the appointment. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Who/what is participating in the appointment. */
  @JsonProperty("relatedPersonId")
  private Id<RelatedPerson> relatedPersonId;

  /** Who/what is participating in the appointment. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;

  /** Who/what is participating in the appointment. */
  @JsonProperty("healthcareServiceId")
  private Id<HealthcareService> healthcareServiceId;

  /** Who/what is participating in the appointment. */
  @JsonProperty("locationId")
  private Id<Location> locationId;
}
