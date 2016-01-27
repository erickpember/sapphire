// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
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

    Observations observations = apiClient.getObservationClient().list(encounterId);

    return getValue(observations, now, encounterId);
  }

  /**
   * Gets value field of daily spontaneous breathing trial. Encapsulates API-independent operation.
   *
   * @param observations
   *     All observations for an encounter
   * @param now
   *     The current time.
   * @param encounterId
   *     The encounter in question.
   * @return
   *     Whether the trial was given, not given or contraindicated.
   */
  public DailySpontaneousBreathingTrialValueEnum getValue(Observations observations, Instant now,
      String encounterId) {
    if (getContraindicatedReason(observations, now, encounterId).isPresent()) {
      return DailySpontaneousBreathingTrialValueEnum.CONTRAINDICATED;
    }

    Instant effectiveLowerBound = now.minus(25, ChronoUnit.HOURS);

    Optional<Observation> freshestSBTAdmin = observations.findFreshest(
        ObservationCodeEnum.SPONTANEOUS_BREATHING_TRIAL.getCode(),
        effectiveLowerBound, null);

    Optional<Observation> freshestPressureSupport = observations.findFreshest(
        ObservationCodeEnum.PRESSURE_SUPPORT.getCode(),
        effectiveLowerBound, null);

    Optional<Observation> freshestFIO2 = observations.findFreshest(
        ObservationCodeEnum.FIO2.getCode(),
        effectiveLowerBound, null);

    Optional<Observation> freshestPEEP = observations.findFreshest(
        ObservationCodeEnum.PEEP.getCode(),
        effectiveLowerBound, null);

    if (freshestSBTAdmin.isPresent()
        && ObservationUtils.getValueAsString(freshestSBTAdmin.get()).equals("Yes")) {
      return DailySpontaneousBreathingTrialValueEnum.GIVEN;
    }

    if (freshestPressureSupport.isPresent()
        && freshestPEEP.isPresent()
        && freshestFIO2.isPresent()
        && ((QuantityDt) freshestPressureSupport.get().getValue()).getValue().compareTo(FIVE) < 1
        && ((QuantityDt) freshestPEEP.get().getValue()).getValue().compareTo(FIVE) < 1
        && ((QuantityDt) freshestFIO2.get().getValue()).getValue().compareTo(FIFTY) < 1) {
      return DailySpontaneousBreathingTrialValueEnum.GIVEN;
    }

    return DailySpontaneousBreathingTrialValueEnum.NOT_GIVEN;
  }

  /**
   * Gets contraindicated reason field of daily spontaneous breathing trial.
   *
   * @param encounterId
   *     The encounter in question.
   * @return optional contraindicated reason, empty if not found
   */
  public Optional<DailySpontaneousBreathingTrialContraindicatedEnum> getContraindicatedReason(
      String encounterId) {

    Instant now = Instant.now(clock);

    Observations observations = apiClient.getObservationClient().list(encounterId);

    return getContraindicatedReason(observations, now, encounterId);
  }

  /**
   * Gets contraindicated reason field of daily spontaneous breathing trial.
   * Encapsulates API-independent logic for easier testing.
   *
   * @param observations
   *     All observations for an encounter
   * @param now
   *     The current time.
   * @param encounterId
   *     The encounter in question.
   * @return optional contraindicated reason, empty if not found
   */
  public Optional<DailySpontaneousBreathingTrialContraindicatedEnum> getContraindicatedReason(
      Observations observations, Instant now, String encounterId) {
    Instant twoHoursAgo = now.minus(2, ChronoUnit.HOURS);
    Instant twentyFiveHoursAgo = now.minus(25, ChronoUnit.HOURS);
    PeriodDt lastTwoHours = new PeriodDt()
        .setStart(new DateTimeDt(Date.from(twoHoursAgo)))
        .setEnd(new DateTimeDt(Date.from(now)));

    Optional<Observation> freshestTrainOfFour = observations.findFreshest(
        ObservationCodeEnum.TRAIN_OF_FOUR.getCode(), twentyFiveHoursAgo, null);

    if (freshestTrainOfFour.isPresent()) {
      switch (ObservationUtils.getValueAsString(freshestTrainOfFour.get())) {
        case "0":
        case "1":
        case "2":
        case "3":
          return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);
      }
    }

    Optional<Observation> freshestSBTContraindication = observations.findFreshest(
        ObservationCodeEnum.SPONTANEOUS_BREATHING_TRIAL_CONTRAINDICATED.getCode(),
        twentyFiveHoursAgo, null);

    if (freshestSBTContraindication.isPresent()) {
      if (ObservationUtils.getValueAsString(freshestSBTContraindication.get())
          .contains("Clinically Unstable")) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.CLINICALLY_UNSTABLE);
      }

      if (ObservationUtils.getValueAsString(freshestSBTContraindication.get())
          .contains("Limited Respiratory Effort")) {
        return Optional.of(
            DailySpontaneousBreathingTrialContraindicatedEnum.LIMITED_RESPIRATORY_EFFORT);
      }

      if (ObservationUtils.getValueAsString(freshestSBTContraindication.get())
          .equals("Other (see comment)")) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.OTHER);
      }
    }

    Optional<Observation> freshestPEEP = observations.findFreshest(ObservationCodeEnum.PEEP
        .getCode(), twentyFiveHoursAgo, null);

    if (freshestPEEP.isPresent()
        && ((QuantityDt) freshestPEEP.get().getValue()).getValue().compareTo(EIGHT) > 0) {
      return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.PEEP_OVER_8);
    }

    Optional<Observation> freshestFIO2 = observations.findFreshest(ObservationCodeEnum.FIO2
        .getCode(), twentyFiveHoursAgo, null);

    if (freshestFIO2.isPresent()
        && ((QuantityDt) freshestFIO2.get().getValue()).getValue().compareTo(FIFTY) > 0) {
      return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.FIO2_OVER_50);
    }

    if (freshestPEEP.isPresent()
        && freshestFIO2.isPresent()
        && freshestSBTContraindication.isPresent()
        && ObservationUtils.getValueAsString(freshestSBTContraindication.get())
        .equals("Respiratory Status")
        && ((QuantityDt) freshestFIO2.get().getValue()).getValue().compareTo(FIFTY) < 0
        && ((QuantityDt) freshestPEEP.get().getValue()).getValue().compareTo(EIGHT) < 0) {
      return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.OTHER);
    }

    List<MedicationAdministration> adminsForEncounter = getAdminsForEncounter(encounterId);

    for (String medsSet : HAVE_THESE_BEEN_ADMINISTERED) {
      boolean administered = MedicationAdministrationUtils.inProgressOrCompletedInTimeFrame(
          adminsForEncounter, lastTwoHours, medsSet);
      if (administered) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);
      }
    }

    for (String medsSet : ARE_THESE_IN_PROGRESS) {
      boolean inProgress = MedicationAdministrationUtils.inProgressInTimeFrame(
          MedicationAdministrationUtils.freshestOfAllOrders(adminsForEncounter).values(),
          lastTwoHours, medsSet);
      if (inProgress) {
        return Optional.of(DailySpontaneousBreathingTrialContraindicatedEnum.RECEIVING_NMBA);
      }
    }

    return Optional.empty();
  }

  /**
   * Wraps API access for medication administrations for easier testing.
   *
   * @param encounterId
   *     Id of the encounter in question.
   * @return a list of medication administrations for the encounter
   */
  protected List<MedicationAdministration> getAdminsForEncounter(String encounterId) {
    return apiClient.
        getMedicationAdministrationClient()
        .search(encounterId);
  }
}
