// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.aog.ADPOLSTImpl;
import com.datafascia.emerge.harms.aog.CodeStatusImpl;
import com.datafascia.emerge.harms.aog.PatientCareConferenceNoteImpl;
import com.datafascia.emerge.ucsf.ADPOLST;
import com.datafascia.emerge.ucsf.AOG;
import com.datafascia.emerge.ucsf.CodeStatus;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.PatientCareConferenceNote;
import com.google.common.base.Strings;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Updates alignment of goals for a patient.
 */
@Slf4j
public class AlignmentOfGoalsUpdater {

  @Inject
  private Clock clock;

  @Inject
  private ADPOLSTImpl adpolstImpl;

  @Inject
  private PatientCareConferenceNoteImpl patientCareConferenceNoteImpl;

  @Inject
  private CodeStatusImpl codeStatusImpl;

  private static AOG getAOG(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    AOG aog = medicalData.getAOG();
    if (aog == null) {
      aog = new AOG();
      medicalData.setAOG(aog);
    }

    return aog;
  }

  private static ADPOLST getADPOLST(HarmEvidence harmEvidence) {
    AOG aog = getAOG(harmEvidence);
    ADPOLST adpolst = aog.getADPOLST();
    if (adpolst == null) {
      adpolst = new ADPOLST();
      aog.setADPOLST(adpolst);
    }

    return adpolst;
  }

  /**
   * Updates advance directive.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateAdvanceDirective(HarmEvidence harmEvidence, Encounter encounter) {
    String patientId = encounter.getPatient().getReference().getIdPart();
    if (Strings.isNullOrEmpty(patientId)) {
      log.warn(
          "Advance directive not updated because encounter {} lacks patient",
          encounter.getId().getIdPart());
      return;
    }

    ADPOLST adpolst = getADPOLST(harmEvidence);
    adpolst.setAdValue(adpolstImpl.haveAdvanceDirective(patientId));
    adpolst.setUpdateTime(Date.from(Instant.now(clock)));
  }

  /**
   * Updates patient care conference note.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updatePatientCareConferenceNote(HarmEvidence harmEvidence, Encounter encounter) {
    String patientId = encounter.getPatient().getReference().getIdPart();

    PatientCareConferenceNote note =
        patientCareConferenceNoteImpl.findPatientCareConferenceNote(patientId)
        .orElse(
            new PatientCareConferenceNote()
                .withValue(false));
    note.setUpdateTime(Date.from(Instant.now(clock)));

    getAOG(harmEvidence).setPatientCareConferenceNote(note);
  }

  /**
   * Updates physician orders for life sustaining treatment.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updatePhysicianOrdersForLifeSustainingTreatment(
      HarmEvidence harmEvidence, Encounter encounter) {

    String patientId = encounter.getPatient().getReference().getIdPart();

    ADPOLST adpolst = getADPOLST(harmEvidence);
    adpolst.setPolstValue(adpolstImpl.havePhysicianOrdersForLifeSustainingTreatment(patientId));
    adpolst.setUpdateTime(Date.from(Instant.now(clock)));
  }

  /**
   * Updates code status.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateCodeStatus(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    getAOG(harmEvidence).setCodeStatus(new CodeStatus()
        .withValue(codeStatusImpl.apply(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock))));
  }
}
