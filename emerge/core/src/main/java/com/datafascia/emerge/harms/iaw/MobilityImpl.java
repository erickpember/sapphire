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
package com.datafascia.emerge.harms.iaw;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.common.inject.Injectors;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.Periods;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.Data;

/**
 * Calculates IAW mobility score.
 */
public class MobilityImpl {

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Result container for an individual mobility result.
   */
  @Data
  public static class MobilityScore {
    private Date updateTime;
    private int levelMobilityAchieved;
    private Date mobilityScoreTime;
    private AssistDeviceEnum assistDevice;
    private NumberOfAssistsEnum numberOfAssists;
    private ClinicianTypeEnum clinicianType;

    MobilityScore() {
      this.levelMobilityAchieved = -1;
    }
  }

  /**
   * Checks if observation is relevant to mobility score.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to mobility score
   */
  public static boolean isRelevant(Observation observation) {
    PeriodDt sinceMidnight =
        Periods.getMidnightToNow(Injectors.getInjector().getInstance(Clock.class));

    return ObservationCodeEnum.MOBILITY_SCORE.isCodeEquals(observation.getCode()) &&
           ObservationUtils.isAfter(observation, sinceMidnight.getStart());
  }

  /**
   * Implements IAW mobility score. Wraps the logic to encapsulate API interaction.
   *
   * @param encounterId
   *     encounter to check.
   * @return A list of mobility scores.
   */
  public List<MobilityScore> getMobility(String encounterId) {
    Observations observations = apiClient.getObservationClient().list(encounterId);
    return getMobility(observations, clock);
  }

  /**
   * Implements IAW mobility score. Returns a maximum of one score for each of three particular
   * clinician types.
   *
   * @param observations
   *     all observations for an encounter.
   * @param clock
   *     clock
   * @return A list of mobility scores.
   */
  public List<MobilityScore> getMobility(Observations observations, Clock clock) {
    MobilityScore rnMobility = new MobilityScore();
    MobilityScore otMobility = new MobilityScore();
    MobilityScore ptMobility = new MobilityScore();
    PeriodDt sinceMidnight = Periods.getMidnightToNow(clock);

    List<Observation> mobilityScores = observations.list(ObservationCodeEnum.MOBILITY_SCORE
        .getCode(), sinceMidnight.getStart().toInstant(), sinceMidnight.getEnd().toInstant());

    for (Observation mobilityScore : mobilityScores) {
      MobilityScore result = new MobilityScore();

      String[] identifierParts = ObservationUtils.getValueAsString(mobilityScore).split(":");
      if (identifierParts.length < 2) {
        continue;
      }
      String[] typeScore = identifierParts[0].split("_");
      if (typeScore.length < 2) {
        continue;
      }
      String clinicianType = typeScore[0];

      MobilityScore oldResult = new MobilityScore();
      switch (clinicianType) {
        case "OT":
          oldResult = otMobility;
          result.setAssistDevice(AssistDeviceEnum.NOT_DOCUMENTED);
          result.setNumberOfAssists(NumberOfAssistsEnum.NOT_DOCUMENTED);
          result.setClinicianType(ClinicianTypeEnum.OT);
          break;
        case "PT":
          oldResult = ptMobility;
          result.setAssistDevice(AssistDeviceEnum.NOT_DOCUMENTED);
          result.setNumberOfAssists(NumberOfAssistsEnum.NOT_DOCUMENTED);
          result.setClinicianType(ClinicianTypeEnum.PT);
          break;
        case "RN":
          oldResult = rnMobility;
          result.setAssistDevice(RNAssist.getBestRnAssistDevice(oldResult.getAssistDevice(),
              mobilityScore));
          result.setNumberOfAssists(RNAssist.getBestRnNumberOfAssists(
              oldResult.getNumberOfAssists(), mobilityScore));
          result.setClinicianType(ClinicianTypeEnum.RN);
          break;
      }

      result.setUpdateTime(Date.from(Instant.now(clock)));
      if (oldResult.getMobilityScoreTime() == null || Dates.toDate(mobilityScore.getEffective())
          .after(oldResult.getMobilityScoreTime())) {
        result.setMobilityScoreTime(Dates.toDate(mobilityScore.getEffective()));
      } else {
        result.setMobilityScoreTime(oldResult.getMobilityScoreTime());
      }

      int mobilityLevel = getMobilityLevelAchieved(mobilityScore);
      if (mobilityLevel > oldResult.getLevelMobilityAchieved()) {
        result.setLevelMobilityAchieved(mobilityLevel);
      } else {
        result.setLevelMobilityAchieved(oldResult.getLevelMobilityAchieved());
      }
      switch (clinicianType) {
        case "OT":
          otMobility = result;
          break;
        case "PT":
          ptMobility = result;
          break;
        case "RN":
          rnMobility = result;
          break;
      }
    }

    List<MobilityScore> nonNullResults = new ArrayList<>();
    for (MobilityScore score : Arrays.asList(rnMobility, otMobility, ptMobility)) {
      if (score.getClinicianType() != null) {
        nonNullResults.add(score);
      }
    }
    return nonNullResults;
  }

  /**
   * Gets mobility level achieved.
   *
   * @param observation
   *     observation to pull from
   * @return level achieved of the freshest mobility event
   */
  public static int getMobilityLevelAchieved(Observation observation) {
    String[] typeScoreParts = getTypeScoreParts(observation);

    if (typeScoreParts != null) {
      if (typeScoreParts.length > 1) {
        return Integer.parseInt(typeScoreParts[1]);
      }
    }

    return -1;
  }

  private static String[] getTypeScoreParts(Observation observation) {
    String[] identifierParts = observation.getValue().toString().split(":");
    if (identifierParts.length > 0) {
      String[] typeScoreParts = identifierParts[0].split("_");
      return typeScoreParts;
    }
    return null;
  }
}
