// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.awt.Image;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The details of a Healthcare Service available at a location.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "HealthcareService")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_HEALTHCARE_SERVICE_ID)
public class HealthcareService {
  /** Identifies the organization across multiple systems. */
  @JsonProperty("@id")
  private Id<HealthcareService> id;

  /** The organization that provides this Healthcare Service. */
  @JsonProperty("providedById")
  private Id<Organization> providedById;

  /** The location where this healthcare service may be provided. */
  @JsonProperty("locationId")
  private Id<Location> locationId;

  /**
   * Identifies the broad category of service. Selecting a category then determines the list of
   * service types that can be selected in the Primary Service Type.
   */
  @JsonProperty("serviceCategory")
  private CodeableConcept serviceCategory;

  /** A specific type of service that may be delivered or performed. */
  @JsonProperty("serviceTypes")
  private List<HealthcareServiceType> serviceTypes;

  /** Further description of the service as it would be presented to a consumer while searching. */
  @JsonProperty("serviceName")
  private String serviceName;

  /**
   * Any additional description of the service and/or any specific issues not covered by the other
   * attributes, which can be displayed as further detail under the serviceName.
   */
  @JsonProperty("comment")
  private String comment;

  /** Extra details about the service that can't be placed in the other fields. */
  @JsonProperty("extraDetails")
  private String extraDetails;

  /** Optional image for quick identification of the service in a list. */
  @JsonProperty("photo")
  private Link<Image> photo;

  /** List of contacts related for this service. If this is empty, refer to Location's contacts. */
  @JsonProperty("telecoms")
  private List<ContactPoint> telecoms;

  /** The code(s) that detail the conditions under which the service is available/offered. */
  @JsonProperty("serviceProvisionCodes")
  private List<CodeableConcept> serviceProvisionCodes;

  /** Does this service have eligibility requirements? */
  @JsonProperty("eligibility")
  private CodeableConcept eligibility;

  /** Describes eligibility conditions for the service. */
  @JsonProperty("eligibilityNote")
  private String eligibilityNote;

  /** Program Names that can be used to categorize the service. */
  @JsonProperty("programNames")
  private List<String> programNames;

  /** Collection of Characteristics (attributes) */
  @JsonProperty("characteristic")
  private List<CodeableConcept> characteristic;

  /** Ways that the service accepts referrals. */
  @JsonProperty("referralMethods")
  private List<CodeableConcept> referralMethods;

  /** The public key of the Organization. */
  @JsonProperty("publicKey")
  private String publicKey;

  /** Indicates if an appointment is required for access to this service. */
  @JsonProperty("appointmentRequired")
  private Boolean appointmentRequired;

  /** A collection of times that the Service Site is available. */
  @JsonProperty("availableTimes")
  private List<HealthcareServiceAvailableTime> availableTimes;

  /** The HealthcareService is not available during these times for these reasons. */
  @JsonProperty("notAvailableTimes")
  private List<HealthcareServiceNotAvailable> notAvailableTimes;

  /** A description of Site availability exceptions, such as public holiday availability. */
  @JsonProperty("availabilityExceptions")
  private String availabilityExceptions;
}
