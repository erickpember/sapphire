// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.ucsf.harm;

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
