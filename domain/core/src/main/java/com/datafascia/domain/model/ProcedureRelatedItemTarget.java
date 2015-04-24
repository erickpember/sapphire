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
 * Part of a Procedure model, an item related to the Procedure
 * of these types:
 * Condition, DiagnosticReport, ImagingStudy, Immunization, MedicationAdministration,
 * MedicationDispense, MedicationPrescription, MedicationStatement
 *
 * These link types are not implemented although part of the Fhir spec:
 * AllergyIntolerance, CarePlan, FamilyMemberHistory, ImmunizationRecommendation
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ProcedureRelatedItemTarget")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ProcedureRelatedItemTarget {
  /** The related item. */
  @JsonProperty("conditionId")
  private Id<Condition> conditionId;

  /** The related item. */
  @JsonProperty("diagnosticReportId")
  private Id<DiagnosticReport> diagnosticReportId;

  /** The related item. */
  @JsonProperty("imagingStudyId")
  private Id<ImagingStudy> imagingStudyId;

  /** The related item. */
  @JsonProperty("immunizationId")
  private Id<Immunization> immunizationId;

  /** The related item. */
  @JsonProperty("medicationAdministrationId")
  private Id<MedicationAdministration> medicationAdministrationId;

  /** The related item. */
  @JsonProperty("medicationDispenseId")
  private Id<MedicationDispense> medicationDispenseId;

  /** The related item. */
  @JsonProperty("medicationPrescriptionId")
  private Id<MedicationPrescription> medicationPrescriptionId;

  /** The related item. */
  @JsonProperty("medicationStatementId")
  private Id<MedicationStatement> medicationStatementId;

  /** The related item. */
  @JsonProperty("observationId")
  private Id<Observation> observationId;

  /** The related item. */
  @JsonProperty("procedureId")
  private Id<Procedure> procedureId;
}
