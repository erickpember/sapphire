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
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
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

  private static final BigDecimal FOUR = new BigDecimal("4");
  private static final BigDecimal FIVE = new BigDecimal("5");
  private static final BigDecimal EIGHT = new BigDecimal("8");
  private static final BigDecimal FIFTY = new BigDecimal("50");
  private static final List<String> HAVE_THESE_BEEN_ADMINISTERED = Arrays.asList(
      MedsSetEnum.INTERMITTENT_CISATRACURIUM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_VECURONIUM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_ROCURONIUM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_PANCURONIUM_IV.getCode());
  private static final List<String> ARE_THESE_IN_PROGRESS = Arrays.asList(
      MedsSetEnum.CONTINUOUS_INFUSION_LORAZEPAM_IV.getCode(),
      MedsSetEnum.CONTINUOUS_INFUSION_MIDAZOLAM_IV.getCode());

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

    Optional<Observation> freshestSBTAdmin = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.SPONTANEOUS_BREATHING_TRIAL.getCode(),
        effectiveLowerBound);

    Optional<Observation> freshestPressureSupport = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.PRESSURE_SUPPORT.getCode(),
        effectiveLowerBound);

    Optional<Observation> freshestFIO2 = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.FIO2.getCode(),
        effectiveLowerBound);

    Optional<Observation> freshestPEEP = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.PEEP.getCode(),
        effectiveLowerBound);

    if (freshestSBTAdmin.isPresent()
        && freshestSBTAdmin.get().getValue().equals("Yes")) {
      return DailySpontaneousBreathingTrialValueEnum.GIVEN;
    }

    if (freshestPressureSupport.isPresent()
        && freshestPEEP.isPresent()
        && freshestFIO2.isPresent()
        && ((QuantityDt) freshestPressureSupport.get().getValue()).getValue().compareTo(FOUR) < 1
        && ((QuantityDt) freshestPEEP.get().getValue()).getValue().compareTo(FIVE) < 1
        && ((QuantityDt) freshestFIO2.get().getValue()).getValue().compareTo(FIFTY) < 1) {
      return DailySpontaneousBreathingTrialValueEnum.GIVEN;
    }

    if (getContraindicatedReason(encounterId).isPresent()) {
      return DailySpontaneousBreathingTrialValueEnum.CONTRAINDICATED;
    }

    return DailySpontaneousBreathingTrialValueEnum.NOT_GIVEN;
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

    Optional<Observation> freshestTrainOfFour = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.TRAIN_OF_FOUR.getCode(), twentyFiveHoursAgo);

    if (freshestTrainOfFour.isPresent()) {
      switch (freshestTrainOfFour.get().getValue().toString()) {
        case "0":
        case "1":
        case "2":
        case "3":
          return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);
      }
    }

    Optional<Observation> freshestSBTContraindication = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.SPONTANEOUS_BREATHING_TRIAL_CONTRAINDICATED.getCode(),
        twentyFiveHoursAgo);

    if (freshestSBTContraindication.isPresent()) {
      if (freshestSBTContraindication.get().getValue().toString().equals("Clinically Unstable")) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.CLINICALLY_UNSTABLE);
      }

      if (freshestSBTContraindication.get().getValue().toString().equals("Other (see comment)")) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.OTHER);
      }
    }

    Optional<Observation> freshestPEEP = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.PEEP.getCode(), twentyFiveHoursAgo);

    if (freshestPEEP.isPresent()
        && ((QuantityDt) freshestPEEP.get().getValue()).getValue().compareTo(EIGHT) > 0) {
      return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.PEEP_OVER_8);
    }

    Optional<Observation> freshestFIO2 = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.FIO2.getCode(), twentyFiveHoursAgo);

    if (freshestFIO2.isPresent()
        && ((QuantityDt) freshestFIO2.get().getValue()).getValue().compareTo(FIFTY) > 0) {
      return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.FIO2_OVER_50);
    }

    if (freshestPEEP.isPresent()
        && freshestFIO2.isPresent()
        && freshestSBTContraindication.isPresent()
        && freshestSBTContraindication.get().getValue().toString().equals("Respiratory Status")
        && ((QuantityDt) freshestFIO2.get().getValue()).getValue().compareTo(FIFTY) < 0
        && ((QuantityDt) freshestPEEP.get().getValue()).getValue().compareTo(EIGHT) < 0) {
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
