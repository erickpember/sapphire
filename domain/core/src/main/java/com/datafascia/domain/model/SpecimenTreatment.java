// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Element in the Specimen model called Treatment, which
 * represents treatment and processing details.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "SpecimenTreatment")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class SpecimenTreatment {

  /** Textual description of the procedure. */
  @JsonProperty("description")
  private String description;

  /** Indicates the treatment or processing step applied to the specimen. */
  @JsonProperty("procedure")
  private CodeableConcept procedure;

  /** Material used in the processing step. */
  @JsonProperty("additives")
  private List<Id<Substance>> additives;
}
