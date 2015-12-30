// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm;

import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.ProcedureRepository;
import com.datafascia.emerge.harms.clabsi.DailyNeedsAssessmentImpl;
import com.datafascia.emerge.ucsf.CLABSI;
import com.datafascia.emerge.ucsf.CentralLine;
import com.datafascia.emerge.ucsf.DailyNeedsAssessment;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.codes.ProcedureCategoryEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * Updates Central Line-Associated Blood Stream Infection data for a patient
 */
public class CentralLineAssociatedBloodStreamInfectionUpdater {

  @Inject
  private Clock clock;

  @Inject
  private DailyNeedsAssessmentImpl dailyNeedsAssessment;

  @Inject
  private ProcedureRepository procedureRepository;

  private static CLABSI getCLABSI(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    CLABSI clabsi = medicalData.getCLABSI();
    if (clabsi == null) {
      clabsi = new CLABSI();
      medicalData.setCLABSI(clabsi);
    }

    return clabsi;
  }

  /**
   * Updates daily needs assessment.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateDailyNeedsAssessment(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    String value = dailyNeedsAssessment.test(encounterId);

    DailyNeedsAssessment newDailyNeedsAssessment = new DailyNeedsAssessment()
        .withPerformed(DailyNeedsAssessment.Performed.fromValue(value))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getCLABSI(harmEvidence).setDailyNeedsAssessment(newDailyNeedsAssessment);
  }

  private static boolean isActiveCentralLine(Procedure procedure) {
    return procedure.getCategory().getCodingFirstRep().getCode()
        .equals(ProcedureCategoryEnum.CENTRAL_LINE.getCode())
        && procedure.getStatusElement().getValueAsEnum() == ProcedureStatusEnum.IN_PROGRESS;
  }

  private static CentralLine.Site formatSite(String bodySiteCode) {
    switch (bodySiteCode) {
      case "Arm":
        return CentralLine.Site.UPPER_ARM;
      case "Internal jugular":
        return CentralLine.Site.INTERNAL_JUGULAR;
      default:
        return CentralLine.Site.fromValue(bodySiteCode);
    }
  }

  private static CentralLine.Side formatSide(String bodySiteSide) {
    switch (bodySiteSide) {
      case "Other":
      case "Unknown":
        return CentralLine.Side.N_A;
      default:
        return CentralLine.Side.fromValue(bodySiteSide);
    }
  }

  private static CentralLine toCentralLine(Procedure procedure) {
    String type = procedure.getCode().getCodingFirstRep().getCode();
    String site = procedure.getBodySiteFirstRep().getCodingFirstRep().getCode();
    String side = procedure.getBodySite().get(1).getCodingFirstRep().getCode();
    DateTimeDt insertionDate = (DateTimeDt) procedure.getPerformed();
    InstantDt updateTime = ResourceMetadataKeyEnum.UPDATED.get(procedure);

    return new CentralLine()
        .withType(CentralLine.Type.fromValue(type))
        .withSite(formatSite(site))
        .withSide(formatSide(side))
        .withInsertionDate((insertionDate != null) ? insertionDate.getValue() : null)
        .withUpdateTime(updateTime.getValue());
  }

  private List<CentralLine> getActiveCentralLines(Encounter encounter) {
    Id<Encounter> encounterId = EncounterRepository.generateId(encounter);
    List<Procedure> procedures = procedureRepository.list(encounterId);
    return procedures.stream()
        .filter(procedure -> isActiveCentralLine(procedure))
        .map(procedure -> toCentralLine(procedure))
        .collect(Collectors.toList());
  }

  /**
   * Updates central line.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateCentralLine(HarmEvidence harmEvidence, Encounter encounter) {
    getCLABSI(harmEvidence).setCentralLine(getActiveCentralLines(encounter));
  }
}
