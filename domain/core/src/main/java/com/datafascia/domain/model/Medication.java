// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This model represents an instance of medication not an Immunization.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Medication")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_MEDICATION_ID)
public class Medication {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Medication> id;

  /** Common / commercial name. */
  @JsonProperty("name")
  private String name;

  /** Code that identifies this medication. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** True if a brand. */
  @JsonProperty("brand")
  private Boolean brand;

  /** Manufacturer of the item. */
  @JsonProperty("manufacturerId")
  private Id<Organization> manufacturerId;

  /** Product | package. */
  @JsonProperty("kind")
  private String kind;

  /** Administrable medication details. */
  @JsonProperty("product")
  private Product product;

  /** Details about the packaged medication. */
  @JsonProperty("medicationPackage")
  private MedicationPackage medicationPackage;
}
