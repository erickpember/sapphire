// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  public final static Map<String, Long> efficacyList = new HashMap<String, Long>() {
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
      put(MedsSetEnum.INTERMITTENT_WARFARIN_ENTERAL.getCode(), TimeUnit.HOURS.toMillis(3));
    }
  };

  /**
   * Determines if the drug has been taken within its effectiveness period.
   *
   * @param timeTaken
   *    The time the drug was administered.
   * @param period
   *    The period, in seconds, that the drug is active.
   * @param clock
   *    Shared configurable timekeeping instance.
   * @return Whether the time taken is within the period of efficacy for the drug to now.
   */
  public static boolean withinDrugPeriod(Date timeTaken, long period, Clock clock) {
    long timeTakenLong = timeTaken.getTime();
    Date lastEffectiveTime = new Date(timeTakenLong + (1000l * period));

    Date now = Date.from(Instant.now(clock));

    // Right now is after the last effective time.
    if (now.after(lastEffectiveTime)) {
      return false;
    }

    return true;
  }

  /**
   * Platelet count <50,000 Implementation
   *
   * @param client
   *     API client.
   * @param encounterId
   *     Relevant encounter ID.
   * @return true if conditions are met
   */
  public static boolean plateletCountLessThan50000(ClientBuilder client, String encounterId) {
    List<Observation> pltObservations = client.getObservationClient().searchObservation(encounterId,
        ObservationCodeEnum.PLT.getCode(), null);
    Observation freshestPltObservation = ObservationUtils.findFreshestObservation(pltObservations);
    if (freshestPltObservation == null || freshestPltObservation.getValue() == null) {
      return false;
    }

    IDatatype quantity = freshestPltObservation.getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(FIFTY) < 0;
    } else {
      throw new NumberFormatException("Observation value is not of type QuantityDt.");
    }
  }

  /**
   * INR >1.5 Implementation
   *
   * @param client
   *     API client.
   * @param encounterId
   *     Relevant encounter ID.
   * @return true if conditions are met
   */
  public static boolean inrOver1point5(ClientBuilder client, String encounterId) {
    List<Observation> pltObservations = client.getObservationClient().searchObservation(encounterId,
        ObservationCodeEnum.INR.getCode(), null);
    Observation freshestInrObservation = ObservationUtils.findFreshestObservation(pltObservations);

    if (freshestInrObservation == null || freshestInrObservation.getValue() == null) {
      return false;
    }

    IDatatype quantity = freshestInrObservation.getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(ONE_POINT_FIVE) > 0;
    } else {
      throw new NumberFormatException("Observation value is not of type QuantityDt.");
    }
  }

  /**
   * aPTT Ratio >1.5 Implementation
   *
   * @param client
   *     API client.
   * @param encounterId
   *     Relevant encounter ID.
   * @return true if conditions are met
   */
  public static boolean aPttRatioOver1point5(ClientBuilder client, String encounterId) {
    List<Observation> pttObservations = client.getObservationClient().searchObservation(encounterId,
        ObservationCodeEnum.PTT.getCode(), null);
    Observation freshestPttObservation = ObservationUtils.findFreshestObservation(pttObservations);

    if (freshestPttObservation == null || freshestPttObservation.getValue() == null) {
      return false;
    }

    IDatatype quantity = freshestPttObservation.getValue();
    if (quantity instanceof QuantityDt) {
      if (freshestPttObservation.getReferenceRangeFirstRep().getText() != null) {
        String[] rangeParts = freshestPttObservation
            .getReferenceRangeFirstRep().getText().split("-");
        if (rangeParts.length > 1) {
          BigDecimal value = ((QuantityDt) quantity).getValue();
          BigDecimal highEnd = new BigDecimal(rangeParts[1]);

          return value.compareTo((highEnd.multiply(ONE_POINT_FIVE))) > 0;
        } else {
          log.warn("Reference range format is invalid: {}",
              freshestPttObservation.getReferenceRangeFirstRep().getText());
        }
      } else {
        log.warn("Reference range is null");
      }
    } else {
      throw new NumberFormatException("Observation value is not of type QuantityDt.");
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
        throw new NumberFormatException("Observation value is not of type QuantityDt.");
      }
    } else if (ObservationUtils.firstIsFresher(freshestClinicalWeight, freshestAdmissionWeight)) {
      IDatatype quantity = freshestClinicalWeight.getValue();
      if (quantity instanceof QuantityDt) {
        return ((QuantityDt) quantity).getValue();
      } else {
        throw new NumberFormatException("Observation value is not of type QuantityDt.");
      }
    } else if (freshestAdmissionWeight != null) {
      IDatatype quantity = freshestAdmissionWeight.getValue();
      if (quantity instanceof QuantityDt) {
        return ((QuantityDt) quantity).getValue();
      } else {
        throw new NumberFormatException("Observation value is not of type QuantityDt.");
      }
    }

    return new BigDecimal("-1");
  }
}
