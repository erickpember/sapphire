// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.neovisionaries.i18n.LanguageCode;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a practitioner.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Practitioner") @IdNamespace(URNFactory.NS_PRACTITIONER_ID)
@ToString(callSuper = true)
public class Practitioner extends Person {
  /** Identifier for the practitioner. */
  @JsonProperty("@id")
  private Id<Practitioner> id;

  /** A list of contacts associated with the practitioner. */
  @JsonProperty("telecoms")
  private List<ContactPoint> telecoms;

  /** Roles which the practitioner may perform. */
  @JsonProperty("practitionerRoles")
  private List<PractitionerRole> practitionerRoles;

  /** Qualifications obtained by training and certification. */
  @JsonProperty("qualifications")
  private List<Qualification> qualifications;

  /** Languages which may be used to communicate with the patient about his or her health. */
  @JsonProperty("communications")
  private List<LanguageCode> communications;
}
