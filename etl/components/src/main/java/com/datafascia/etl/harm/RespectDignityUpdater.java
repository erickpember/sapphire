// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.respectdignity.ParticipantFinder;
import com.datafascia.emerge.ucsf.CareProvider;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.RespectDignity;
import com.datafascia.emerge.ucsf.valueset.PractitionerRoleEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import javax.inject.Inject;

/**
 * Updates respect and dignity data for a patient.
 */
public class RespectDignityUpdater {

  @Inject
  private Clock clock;

  @Inject
  private ParticipantFinder participantFinder;

  private static RespectDignity getRespectDignity(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    RespectDignity respectDignity = medicalData.getRespectDignity();
    if (respectDignity == null) {
      respectDignity = new RespectDignity();
      medicalData.setRespectDignity(respectDignity);
    }

    return respectDignity;
  }

  /**
   * Updates ICU attending.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateIcuAttending(HarmEvidence harmEvidence, Encounter encounter) {
    participantFinder.findByPractitionerRole(encounter, PractitionerRoleEnum.ICU_ATTENDING)
        .ifPresent(careProvider -> {
          CareProvider newIcuAttending = new CareProvider()
              .withName(careProvider.getName())
              .withAddedToCareTeam(careProvider.getPeriodStart())
              .withUpdateTime(Date.from(Instant.now(clock)));

          getRespectDignity(harmEvidence).setIcuAttending(newIcuAttending);
        });
  }

  /**
   * Updates primary attending.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updatePrimaryAttending(HarmEvidence harmEvidence, Encounter encounter) {
    participantFinder.findByPractitionerRole(encounter, PractitionerRoleEnum.PRIMARY_CARE_ATTENDING)
        .ifPresent(careProvider -> {
          CareProvider newPrimaryAttending = new CareProvider()
              .withName(careProvider.getName())
              .withAddedToCareTeam(careProvider.getPeriodStart())
              .withUpdateTime(Date.from(Instant.now(clock)));

          getRespectDignity(harmEvidence).setPrimaryServiceAttending(newPrimaryAttending);
        });
  }

  /**
   * Updates clinical nurse.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateClinicalNurse(HarmEvidence harmEvidence, Encounter encounter) {
    participantFinder.findByPractitionerRole(encounter, PractitionerRoleEnum.CLINICAL_NURSE)
        .ifPresent(careProvider -> {
          CareProvider newClinicalNurse = new CareProvider()
              .withName(careProvider.getName())
              .withAddedToCareTeam(careProvider.getPeriodStart())
              .withUpdateTime(Date.from(Instant.now(clock)));

          getRespectDignity(harmEvidence).setRN(newClinicalNurse);
        });
  }
}
