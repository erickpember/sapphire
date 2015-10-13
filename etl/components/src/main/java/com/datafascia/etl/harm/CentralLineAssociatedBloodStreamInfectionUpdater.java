// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.ProcedureRepository;
import com.datafascia.emerge.ucsf.CLABSI;
import com.datafascia.emerge.ucsf.CentralLine;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.codes.ProcedureCategoryEnum;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * Updates Central Line-Associated Blood Stream Infection data for a patient
 */
public class CentralLineAssociatedBloodStreamInfectionUpdater {

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

  private static boolean isActiveCentralLine(Procedure procedure) {
    return procedure.getCategory().getCodingFirstRep().getCode()
        .equals(ProcedureCategoryEnum.CENTRAL_LINE.getCode())
        && procedure.getStatusElement().getValueAsEnum() == ProcedureStatusEnum.IN_PROGRESS;
  }

  private static CentralLine toCentralLine(Procedure procedure) {
    String type = procedure.getCode().getCodingFirstRep().getCode();
    String site = procedure.getBodySiteFirstRep().getCodingFirstRep().getCode();
    String side = procedure.getBodySite().get(1).getCodingFirstRep().getCode();
    DateTimeDt insertionDate = (DateTimeDt) procedure.getPerformed();
    InstantDt updateTime = ResourceMetadataKeyEnum.UPDATED.get(procedure);

    return new CentralLine()
        .withType(CentralLine.Type.fromValue(type))
        .withSite(CentralLine.Site.fromValue(site))
        .withSide(CentralLine.Side.fromValue(side))
        .withInsertionDate(insertionDate.getValue())
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
   * Updates Central Line-Associated Blood Stream Infection data.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateCentralLine(HarmEvidence harmEvidence, Encounter encounter) {
    CLABSI clabsi = getCLABSI(harmEvidence);
    clabsi.setCentralLine(getActiveCentralLines(encounter));
  }
}