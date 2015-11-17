// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.iaw.MobilityScore;
import com.datafascia.emerge.harms.iaw.RNAssist;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.IAW;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.MobilityAchieved;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import javax.inject.Inject;

/**
 * Updates intensive care unit acquired weakness data for a patient.
 */
public class IntensiveCareUnitAcquiredWeaknessUpdater {

  @Inject
  private Clock clock;

  @Inject
  private MobilityScore mobilityScore;

  private static IAW getIAW(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    IAW iaw = medicalData.getIAW();
    if (iaw == null) {
      iaw = new IAW();
      medicalData.setIAW(iaw);
    }

    return iaw;
  }

  /**
   * Updates intensive care unit acquired weakness data.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void update(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    mobilityScore.getFreshestObservation(encounterId)
        .ifPresent(observation -> {
          int level = MobilityScore.getMobilityLevelAchieved(observation);
          Date scoreTime = MobilityScore.getMobilityScoreTime(observation);
          String clinicianType = MobilityScore.getClinicianType(observation);
          String assistDevice = RNAssist.getAssistDevices(observation);
          String numberOfAssists = RNAssist.getNumberOfAssists(observation);

          MobilityAchieved mobilityAchieved = new MobilityAchieved()
              .withLevelMobilityAchieved(
                  level)
              .withMobilityScoreTime(
                  scoreTime)
              .withClinicianType(
                  MobilityAchieved.ClinicianType.fromValue(clinicianType))
              .withAssistDevice(
                  MobilityAchieved.AssistDevice.fromValue(assistDevice))
              .withNumberOfAssists(
                  MobilityAchieved.NumberOfAssists.fromValue(numberOfAssists))
              .withUpdateTime(Date.from(Instant.now(clock)));

          getIAW(harmEvidence).setMobilityAchieved(mobilityAchieved);
        });
  }
}
