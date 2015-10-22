// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.DailySpontaneousBreathingTrialContraindicatedEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.DailySpontaneousBreathingTrialValueEnum;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Implements daily spontaneous breathing trial
 */
public class DailySpontaneousBreathingTrialImpl {

  private static final BigDecimal FIVE = new BigDecimal("5");
  private static final BigDecimal EIGHT = new BigDecimal("8");
  private static final BigDecimal FIFTY = new BigDecimal("50");
  private static final List<String> HAVE_THESE_BEEN_ADMINISTERED = Arrays.asList(
      "Intermittent Cisatracurium IV",
      "Intermittent Vecuronium IV",
      "Intermittent Rocuronium IV",
      "Intermittent Pancuronium IV");
  private static final List<String> ARE_THESE_IN_PROGRESS = Arrays.asList(
      "Continuous Infusion Lorazepam IV",
      "Continuous Infusion Midazolam IV");

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Gets value field of daily spontaneous breathing trial.
   *
   * @param encounterId
   *     encounter to search
   * @return
   *     Whether the trial was given, not given or contraindicated.
   */
  public DailySpontaneousBreathingTrialValueEnum getValue(String encounterId) {
    Instant now = Instant.now(clock);
    Date effectiveLowerBound = Date.from(now.minus(25, ChronoUnit.HOURS));

    Observation freshestSBTAdmin = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.SPONTANEOUS_BREATHING_TRIAL.getCode(),
        effectiveLowerBound);

    Observation freshestPressureSupport = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.PRESSURE_SUPPORT.getCode(),
        effectiveLowerBound);

    Observation freshestFIO2 = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.FIO2.getCode(),
        effectiveLowerBound);

    Observation freshestPEEP = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.PEEP.getCode(),
        effectiveLowerBound);

    if (freshestSBTAdmin != null && freshestSBTAdmin.getValue().toString().equals("Yes")) {
      return DailySpontaneousBreathingTrialValueEnum.GIVEN;
    }

    if (freshestPressureSupport != null && freshestPEEP != null && freshestFIO2 != null
        && ((QuantityDt) freshestPressureSupport.getValue()).getValue().compareTo(FIVE) < 0
        && ((QuantityDt) freshestPEEP.getValue()).getValue().compareTo(FIVE) < 0
        && ((QuantityDt) freshestFIO2.getValue()).getValue().compareTo(FIFTY) < 0) {
      return DailySpontaneousBreathingTrialValueEnum.GIVEN;
    }

    return DailySpontaneousBreathingTrialValueEnum.CONTRAINDICATED;
  }

  /**
   * Gets contraindicated reason field of daily spontaneous breathing trial.
   *
   * @param encounterId
   *     encounter to search
   * @return optional contraindicated reason, empty if not found
   */
  public Optional<DailySpontaneousBreathingTrialContraindicatedEnum> getContraindicatedReason(
      String encounterId) {

    Instant now = Instant.now(clock);
    Date twoHoursAgo = Date.from(now.minus(2, ChronoUnit.HOURS));
    Date twentyFiveHoursAgo = Date.from(now.minus(25, ChronoUnit.HOURS));
    PeriodDt lastTwoHours = new PeriodDt()
        .setStart(new DateTimeDt(twoHoursAgo))
        .setEnd(new DateTimeDt(Date.from(now)));

    Observation freshestTrainOfFour = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.TRAIN_OF_FOUR.getCode(), twentyFiveHoursAgo);

    if (freshestTrainOfFour != null) {
      switch (freshestTrainOfFour.getValue().toString()) {
        case "0":
        case "1":
        case "2":
        case "3":
          return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);
      }
    }

    Observation freshestSBTContraindication = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.SPONTANEOUS_BREATHING_TRIAL_CONTRAINDICATED.getCode(),
        twentyFiveHoursAgo);

    if (freshestSBTContraindication != null) {
      if (freshestSBTContraindication.getValue().toString().equals("Clinically Unstable")) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.CLINICALLY_UNSTABLE);
      }

      if (freshestSBTContraindication.getValue().toString().equals("Other (see comment)")) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.OTHER);
      }
    }

    Observation freshestPEEP = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.PEEP.getCode(), twentyFiveHoursAgo);

    if (freshestPEEP != null && ((QuantityDt) freshestPEEP.getValue()).getValue().compareTo(EIGHT)
        > 0) {
      return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.PEEP_OVER_8);
    }

    Observation freshestFIO2 = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.FIO2.getCode(), twentyFiveHoursAgo);

    if (freshestFIO2 != null && ((QuantityDt) freshestFIO2.getValue()).getValue().compareTo(FIFTY)
        > 0) {
      return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.FIO2_OVER_50);
    }

    if (freshestPEEP != null && freshestFIO2 != null && freshestSBTContraindication != null
        && freshestSBTContraindication.getValue().toString().equals("Respiratory Status")
        && ((QuantityDt) freshestFIO2.getValue()).getValue().compareTo(FIFTY) < 0
        && ((QuantityDt) freshestPEEP.getValue()).getValue().compareTo(EIGHT) < 0) {
      return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.OTHER);
    }

    for (String medsSet : HAVE_THESE_BEEN_ADMINISTERED) {
      boolean administered = MedicationAdministrationUtils.inProgressOrCompletedInTimeFrame(
          apiClient, encounterId, lastTwoHours, medsSet);
      if (administered) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);
      }
    }

    for (String medsSet : ARE_THESE_IN_PROGRESS) {
      boolean inProgress = MedicationAdministrationUtils.inProgressInTimeFrame(
          apiClient, encounterId, lastTwoHours, medsSet);
      if (inProgress) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);
      }
    }

    return Optional.empty();
  }
}
