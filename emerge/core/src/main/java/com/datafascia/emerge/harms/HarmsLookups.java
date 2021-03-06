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
package com.datafascia.emerge.harms;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * A collection of simple API lookups commonly used as components in harms logic.
 */
@Slf4j
public class HarmsLookups {
  private static final BigDecimal ONE_POINT_FIVE = new BigDecimal("1.5");
  private static final BigDecimal FIFTY = new BigDecimal("50");
  private static final BigDecimal OUNCES_PER_KILOGRAM = new BigDecimal("35.274");

  /*
   * Drug efficacy durations.  See the current UCSF Emerge Medication Info Master spreadsheet,
   * VTE Duration of Anitcoagulation sheet, for details.
   */
  public final static Map<String, Long> EFFICACY_LIST = new HashMap<String, Long>() {
    {
      put(MedsSetEnum.CONTINUOUS_INFUSION_HEPARIN_IV.getCode(), TimeUnit.HOURS.toMillis(10));
      put(MedsSetEnum.INTERMITTENT_HEPARIN_SC.getCode(), TimeUnit.HOURS.toMillis(12));
      put(MedsSetEnum.CONTINUOUS_INFUSION_ARGATROBAN_IV.getCode(), TimeUnit.HOURS.toMillis(5));
      put(MedsSetEnum.CONTINUOUS_INFUSION_BIVALIRUDIAN_IV.getCode(), TimeUnit.HOURS.toMillis(5));
      put(MedsSetEnum.INTERMITTENT_ENOXAPARIN.getCode(), TimeUnit.HOURS.toMillis(25));
      put(MedsSetEnum.INTERMITTENT_DABIGATRAN_ENTERAL.getCode(), TimeUnit.HOURS.toMillis(25));
      put(MedsSetEnum.INTERMITTENT_APIXABAN_ENTERAL.getCode(), TimeUnit.HOURS.toMillis(13));
      put(MedsSetEnum.INTERMITTENT_RIVAROXABAN_ENTERAL.getCode(), TimeUnit.HOURS.toMillis(25));
      put(MedsSetEnum.INTERMITTENT_EDOXABAN_ENTERAL.getCode(), TimeUnit.HOURS.toMillis(25));
      put(MedsSetEnum.INTERMITTENT_FONDAPARINUX_SC.getCode(), TimeUnit.HOURS.toMillis(25));
      put(MedsSetEnum.INTERMITTENT_WARFARIN_ENTERAL.getCode(), TimeUnit.DAYS.toMillis(3));
    }
  };

  /**
   * Determines if the drug has been taken within its effectiveness period.
   *
   * @param timeTaken
   *    The time the drug was administered.
   * @param period
   *    The period, in milliseconds, that the drug is active.
   * @param now
   *    Current time.
   * @return Whether the time taken is within the period of efficacy for the drug to now.
   */
  public static boolean withinDrugPeriod(Date timeTaken, long period, Instant now) {
    long timeTakenLong = timeTaken.getTime();
    Date lastEffectiveTime = new Date(timeTakenLong + period);

    return !Date.from(now).after(lastEffectiveTime);
  }

  /**
   * Platelet count <50,000 Implementation
   *
   * @param observations
   *     Wrapper containing observations for the encounter.
   * @param effectiveLower
   *     Lower time bound for the observation query.
   * @return true if conditions are met
   */
  public static boolean plateletCountLessThan50000(
      Observations observations, Instant effectiveLower) {

    Optional<Observation> freshestPltObservation = observations.findFreshest(
        ObservationCodeEnum.PLT.getCode(), effectiveLower, null);

    if (!freshestPltObservation.isPresent() || freshestPltObservation.get().getValue().isEmpty()) {
      return false;
    }

    IDatatype quantity = freshestPltObservation.get().getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(FIFTY) < 0;
    } else {
      log.warn("Observation value is not of type QuantityDt: " + quantity);
      return false;
    }
  }

  /**
   * INR >1.5 Implementation
   *
   * @param observations
   *     Wrapper containing observations for the encounter.
   * @param effectiveLower
   *     Lower time bound for the observation query.
   * @return true if conditions are met
   */
  public static boolean inrOver1point5(Observations observations, Instant effectiveLower) {
    Optional<Observation> freshestInrObservation = observations.findFreshest(
        ObservationCodeEnum.INR.getCode(), effectiveLower, null);

    if (!freshestInrObservation.isPresent() || freshestInrObservation.get().getValue().isEmpty()) {
      return false;
    }

    IDatatype quantity = freshestInrObservation.get().getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(ONE_POINT_FIVE) > 0;
    } else {
      log.warn("Observation value is not of type QuantityDt: " + quantity);
      return false;
    }
  }

  /**
   * aPTT Ratio >1.5 Implementation
   *
   * @param observations
   *     Wrapper containing observations for the encounter.
   * @param effectiveLower
   *     Lower time bound for the observation query.
   * @return true if conditions are met
   */
  public static boolean aPttRatioOver1point5(Observations observations, Instant effectiveLower) {
    Optional<Observation> freshestPttObservation = observations.findFreshest(
        ObservationCodeEnum.PTT.getCode(), effectiveLower, null);

    if (!freshestPttObservation.isPresent() || freshestPttObservation.get().getValue().isEmpty()) {
      return false;
    }

    IDatatype quantity = freshestPttObservation.get().getValue();
    if (quantity instanceof QuantityDt) {
      if (!freshestPttObservation.get().getReferenceRangeFirstRep().getText().isEmpty()) {
        String[] rangeParts = freshestPttObservation.get()
            .getReferenceRangeFirstRep().getText().split("-");
        if (rangeParts.length > 1) {
          BigDecimal value = ((QuantityDt) quantity).getValue();
          BigDecimal highEnd = new BigDecimal(rangeParts[1]);

          return value.compareTo((highEnd.multiply(ONE_POINT_FIVE))) > 0;
        } else {
          log.warn("Reference range format is invalid: {}",
              freshestPttObservation.get().getReferenceRangeFirstRep().getText());
        }
      } else {
        log.warn("Reference range is null");
      }
    } else {
      log.warn("Observation value is not of type QuantityDt: " + quantity);
    }

    return false;
  }

  /**
   * Patient Weight Implementation
   *
   * @param client
   *     API client.
   * @param encounterId
   *     Relevant encounter ID.
   * @return the patients weight
   */
  public static BigDecimal getPatientWeight(ClientBuilder client, String encounterId) {
    List<Observation> dosingWeight = client.getObservationClient().searchObservation(encounterId,
        ObservationCodeEnum.DOSING_WEIGHT.getCode(), null);
    Observation freshestDosingWeight = ObservationUtils.findFreshestObservation(dosingWeight);

    List<Observation> clinicalWeight = client.getObservationClient().searchObservation(encounterId,
        ObservationCodeEnum.CLINICAL_WEIGHT.getCode(), null);
    Observation freshestClinicalWeight = ObservationUtils.findFreshestObservation(clinicalWeight);

    List<Observation> admissionWeight = client.getObservationClient().searchObservation(encounterId,
        ObservationCodeEnum.ADMISSION_WEIGHT.getCode(), null);
    Observation freshestAdmissionWeight = ObservationUtils.findFreshestObservation(admissionWeight);

    if (ObservationUtils.firstIsFresher(freshestDosingWeight, freshestClinicalWeight)) {
      IDatatype quantity = freshestDosingWeight.getValue();
      if (quantity instanceof QuantityDt) {
        return ((QuantityDt) quantity).getValue().multiply(OUNCES_PER_KILOGRAM);
      } else {
        log.warn("Observation value is not of type QuantityDt: " + quantity);
        return new BigDecimal("-1");
      }
    } else if (ObservationUtils.firstIsFresher(freshestClinicalWeight, freshestAdmissionWeight)) {
      IDatatype quantity = freshestClinicalWeight.getValue();
      if (quantity instanceof QuantityDt) {
        return ((QuantityDt) quantity).getValue();
      } else {
        log.warn("Observation value is not of type QuantityDt: " + quantity);
        return new BigDecimal("-1");
      }
    } else if (freshestAdmissionWeight != null) {
      IDatatype quantity = freshestAdmissionWeight.getValue();
      if (quantity instanceof QuantityDt) {
        return ((QuantityDt) quantity).getValue();
      } else {
        log.warn("Observation value is not of type QuantityDt: " + quantity);
        return new BigDecimal("-1");
      }
    }

    return new BigDecimal("-1");
  }
}
