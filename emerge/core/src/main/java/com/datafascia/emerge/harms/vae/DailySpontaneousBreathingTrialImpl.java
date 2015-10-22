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
import com.datafascia.emerge.ucsf.codes.ventilation.DailySBTContraindicatedEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.DailySBTValueEnum;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Implements Daily Spontaneous Breathing Trial
 */
public class DailySpontaneousBreathingTrialImpl {
  @Inject
  private ClientBuilder apiClient;

  private static final BigDecimal FIVE = new BigDecimal("5");
  private static final BigDecimal EIGHT = new BigDecimal("8");
  private static final BigDecimal FIFTY = new BigDecimal("50");

  // Private constructor disallows creating instances of this class.
  private DailySpontaneousBreathingTrialImpl() {
  }

  /**
   * Implements the value field of Daily Spontaneous Breathing Trial
   *
   * @param encounterId
   *     Relevant encounter ID.
   * @return
   *     Whether the trial was given, not given or contraindicated.
   */
  public String value(String encounterId) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -25);
    Date twentyFiveHoursAgo = cal.getTime();

    Observation freshestSBTAdmin = ObservationUtils.getFreshestByCodeAfterTime(apiClient,
        encounterId,
        ObservationCodeEnum.SBT.getCode(), twentyFiveHoursAgo);

    Observation freshestPressureSupport = ObservationUtils.getFreshestByCodeAfterTime(apiClient,
        encounterId,
        ObservationCodeEnum.PRESSURE_SUPPORT.getCode(), twentyFiveHoursAgo);

    Observation freshestFIO2 = ObservationUtils.getFreshestByCodeAfterTime(apiClient, encounterId,
        ObservationCodeEnum.FIO2.getCode(), twentyFiveHoursAgo);

    Observation freshestPEEP = ObservationUtils.getFreshestByCodeAfterTime(apiClient, encounterId,
        ObservationCodeEnum.PEEP.getCode(), twentyFiveHoursAgo);

    if (freshestSBTAdmin != null && freshestSBTAdmin.getValue().toString().equals("Yes")) {
      switch (freshestSBTAdmin.getValue().toString()) {
        case "Yes":
          return DailySBTValueEnum.GIVEN.getCode();
        case "No":
          return DailySBTValueEnum.NOT_GIVEN.getCode();
      }
    }

    if (freshestPressureSupport != null && freshestPEEP != null && freshestFIO2 != null
        && ((QuantityDt) freshestPressureSupport.getValue()).getValue().compareTo(FIVE) < 0
        && ((QuantityDt) freshestPEEP.getValue()).getValue().compareTo(FIVE) < 0
        && ((QuantityDt) freshestFIO2.getValue()).getValue().compareTo(FIFTY) < 0) {
      return DailySBTValueEnum.GIVEN.getCode();
    }

    return DailySBTValueEnum.CONTRAINDICATED.getCode();
  }

  /**
   * Implements the contraindicated field of Daily Spontaneous Breathing Trial
   *
   * @param encounterId
   *     Relevant encounter ID.
   * @return
   *     Why the spontaneous breathing trial is contraindicated.
   */
  public String contraindicated(String encounterId) {
    Calendar cal = Calendar.getInstance();

    Date now = cal.getTime();
    cal.add(Calendar.HOUR, -2);
    Date twoHoursAgo = cal.getTime();
    cal.add(Calendar.HOUR, -23);
    Date twentyFiveHoursAgo = cal.getTime();
    PeriodDt lastTwoHours = new PeriodDt().setStart(new DateTimeDt(twoHoursAgo)).setEnd(
        new DateTimeDt(now));

    Observation freshestTrainOfFour = ObservationUtils.getFreshestByCodeAfterTime(apiClient,
        encounterId,
        ObservationCodeEnum.TRAIN_OF_FOUR.getCode(), twentyFiveHoursAgo);

    if (freshestTrainOfFour != null) {
      switch (freshestTrainOfFour.getValue().toString()) {
        case "0":
        case "1":
        case "2":
        case "3":
          return DailySBTContraindicatedEnum.RECEIVING_NMBA.getCode();
      }
    }

    Observation freshestSBTContraindication = ObservationUtils.getFreshestByCodeAfterTime(apiClient,
        encounterId, ObservationCodeEnum.SBT_CONTRAINDICATED.getCode(), twentyFiveHoursAgo);

    if (freshestSBTContraindication != null) {
      if (freshestSBTContraindication.getValue().toString().equals("Clinically Unstable")) {
        return DailySBTContraindicatedEnum.CLINICALLY_UNSTABLE.getCode();
      }

      if (freshestSBTContraindication.getValue().toString().equals("Other (see comment)")) {
        return DailySBTContraindicatedEnum.OTHER.getCode();
      }
    }

    Observation freshestPEEP = ObservationUtils.getFreshestByCodeAfterTime(apiClient, encounterId,
        ObservationCodeEnum.PEEP.getCode(), twentyFiveHoursAgo);

    if (freshestPEEP != null && ((QuantityDt) freshestPEEP.getValue()).getValue().compareTo(EIGHT)
        > 0) {
      return DailySBTContraindicatedEnum.PEEP_OVER_8.getCode();
    }

    Observation freshestFIO2 = ObservationUtils.getFreshestByCodeAfterTime(apiClient, encounterId,
        ObservationCodeEnum.FIO2.getCode(), twentyFiveHoursAgo);

    if (freshestFIO2 != null && ((QuantityDt) freshestFIO2.getValue()).getValue().compareTo(FIFTY)
        > 0) {
      return DailySBTContraindicatedEnum.FIO2_OVER_50.getCode();
    }

    if (freshestPEEP != null && freshestFIO2 != null && freshestSBTContraindication != null
        && freshestSBTContraindication.getValue().toString().equals("Respiratory Status")
        && ((QuantityDt) freshestFIO2.getValue()).getValue().compareTo(FIFTY) < 0
        && ((QuantityDt) freshestPEEP.getValue()).getValue().compareTo(EIGHT) < 0) {
      return DailySBTContraindicatedEnum.OTHER.getCode();
    }

    List<String> haveTheseBeenAdministered = Arrays.asList("Intermittent Cisatracurium IV",
        "Intermittent Vecuronium IV", "Intermittent Rocuronium IV", "Intermittent Pancuronium IV");

    for (String medsSet : haveTheseBeenAdministered) {
      if (MedicationAdministrationUtils.inProgressOrCompletedInTimeFrame(apiClient, encounterId,
          lastTwoHours,
          medsSet)) {
        return DailySBTContraindicatedEnum.RECEIVING_NMBA.getCode();
      }
    }

    List<String> areTheseInProgress = Arrays.asList("Continuous Infusion Lorazepam IV",
        "Continuous Infusion Midazolam IV");

    for (String medsSet : areTheseInProgress) {
      if (MedicationAdministrationUtils.inProgressInTimeFrame(apiClient, encounterId, lastTwoHours,
          medsSet)) {
        return DailySBTContraindicatedEnum.RECEIVING_NMBA.getCode();
      }
    }

    return null;
  }
}
