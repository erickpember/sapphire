// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import com.datafascia.emerge.harms.aog.ADPOLSTImpl;
import com.datafascia.emerge.harms.aog.PatientCareConferenceNoteImpl;
import com.datafascia.emerge.ucsf.ADPOLST;
import com.datafascia.emerge.ucsf.AOG;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.codes.FlagCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import javax.inject.Inject;

/**
 * Updates alignment of goals for a patient.
 */
public class AlignmentOfGoalsUpdater {

  @Inject
  private Clock clock;

  @Inject
  private ADPOLSTImpl adpolstImpl;

  @Inject
  private PatientCareConferenceNoteImpl patientCareConferenceNoteImpl;

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

  private void updateAdvanceDirective(HarmEvidence harmEvidence, String patientId) {
    ADPOLST adpolst = getADPOLST(harmEvidence);
    adpolst.setAdValue(adpolstImpl.haveAdvanceDirective(patientId));
    adpolst.setUpdateTime(Date.from(Instant.now(clock)));
  }

  private void updatePatientCareConferenceNote(HarmEvidence harmEvidence, String patientId) {
    patientCareConferenceNoteImpl.findPatientCareConferenceNote(patientId)
        .ifPresent(note -> {
            note.setUpdateTime(Date.from(Instant.now(clock)));
            getAOG(harmEvidence).setPatientCareConferenceNote(note);
          });
  }

  private void updatePhysicianOrdersForLifeSustainingTreatment(
      HarmEvidence harmEvidence, String patientId) {

    ADPOLST adpolst = getADPOLST(harmEvidence);
    adpolst.setPolstValue(adpolstImpl.havePhysicianOrdersForLifeSustainingTreatment(patientId));
    adpolst.setUpdateTime(Date.from(Instant.now(clock)));
  }

  /**
   * Updates alignment of goals.
   *
   * @param harmEvidence
   *     to modify
   * @param flag
   *     flag
   * @param encounter
   *     encounter
   */
  public void update(HarmEvidence harmEvidence, Flag flag, Encounter encounter) {
    String patientId = encounter.getPatient().getReference().getIdPart();

    FlagCodeEnum.of(flag.getCode().getCodingFirstRep().getCode())
        .ifPresent(flagCode -> {
            switch (flagCode) {
              case ADVANCE_DIRECTIVE:
                updateAdvanceDirective(harmEvidence, patientId);
                break;
              case PATIENT_CARE_CONFERENCE_NOTE:
                updatePatientCareConferenceNote(harmEvidence, patientId);
                break;
              case PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT:
                updatePhysicianOrdersForLifeSustainingTreatment(harmEvidence, patientId);
                break;
            }
          });
  }
}
