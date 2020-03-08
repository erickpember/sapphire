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
import com.datafascia.emerge.harms.iaw.MobilityImpl;
import com.datafascia.emerge.harms.iaw.MobilityImpl.MobilityScore;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.IAW;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.Mobility;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Updates intensive care unit acquired weakness data for a patient.
 */
public class IntensiveCareUnitAcquiredWeaknessUpdater {

  @Inject
  private MobilityImpl mobilityScore;

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
    List<MobilityScore> scores = mobilityScore.getMobility(encounterId);
    List<Mobility> results = new ArrayList<>();
    for (MobilityScore score : scores) {
      Mobility result = new Mobility();
      result.setAssistDevice((score.getAssistDevice() != null) ? Mobility.AssistDevice.fromValue(
          score.getAssistDevice().getCode()) : null);
      result.setClinicianType((score.getClinicianType() != null) ? Mobility.ClinicianType.fromValue(
          score.getClinicianType().getCode()) : null);
      result.setLevelMobilityAchieved(score.getLevelMobilityAchieved());
      result.setMobilityScoreTime(score.getMobilityScoreTime());
      result.setNumberOfAssists((score.getNumberOfAssists() != null) ? Mobility.NumberOfAssists
          .fromValue(score.getNumberOfAssists().getCode()) : null);
      result.setUpdateTime(score.getUpdateTime());
      results.add(result);
    }
    if (!results.isEmpty()) {
      getIAW(harmEvidence).setMobility(results);
    }
  }
}
