// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTimeConstants;

/**
 * A collection of simple API lookups commonly used as components in harms logic.
 */
public class HarmsLookups {
  private static final BigDecimal ONE_POINT_THREE = new BigDecimal("1.3");
  private static final BigDecimal ONE_POINT_FIVE = new BigDecimal("1.5");
  private static final BigDecimal FIFTY_THOUSAND = new BigDecimal("50000");

  // Drug efficacy durations.
  public final static Map<String, Integer> efficacyList = new HashMap<String, Integer>() {
    {
      // Administered within 10 hours
      put("Heparin infusion", 10 * DateTimeConstants.MILLIS_PER_HOUR);
      // Administered within 12 hours + 1h buffer = 13 hours
      put("Heparin injection (ppx)", 12 * DateTimeConstants.MILLIS_PER_HOUR);
      /* Administered within 5 hours. Did 5x normal half-life in this scenario as the drug would not
       * be routinely used in hepatic impairment. */
      put("Argatroban infusion", 5 * DateTimeConstants.MILLIS_PER_HOUR);
      /* Administered within 5 hours. Used 5x half-life for gfr 10-29 since would not routinely be
       * used in patients with ESRD off dialysis. */
      put("Bivalirudin infusion", 5 * DateTimeConstants.MILLIS_PER_HOUR);
      /* Administered within 24 hours + 1 h buffer = 25 hours. Initial idea was to pull by
       * administration frequency + weight; administered within 12 hours (1 mg/kg doses) or 24 hours
       * (1.5 mg/kg doses + prophylactic doses) */
      put("Enoxaparin injection (ppx) ", 25 * DateTimeConstants.MILLIS_PER_HOUR);
      /* Administered within 24 hours + 1 h buffer = 25 hours. Initial idea was to pull by
       * administration frequency + weight; administered within 12 hours (1 mg/kg doses) or 24 hours
       * (1.5 mg/kg doses + prophylactic doses) */
      put("Enoxaparin injection (therapeutic) ", 25 * DateTimeConstants.MILLIS_PER_HOUR);
      // Administered within 24 hours + 1 h buffer = 25 hours
      put("Dabigatran oral", 25 * DateTimeConstants.MILLIS_PER_HOUR);
      // Administered within 12 hours + 1h buffer = 13 hours
      put("Apixaban oral", 13 * DateTimeConstants.MILLIS_PER_HOUR);
      /* Administered within 24 hours + 1 h buffer = 25 hours. For drugs given therapeutically at 12
       * and 24 hour intervals, should we pull based on the 24h + buffer to be most inclusive? I'd
       * hate to prompt earlier admin in someone who is still therapeutic. */
      put("Rivaroxaban", 25 * DateTimeConstants.MILLIS_PER_HOUR);
      // Administered within 24 hours + 1 h buffer = 25 hours
      put("Edoxaban", 25 * DateTimeConstants.MILLIS_PER_HOUR);
      // Administered within 24 hours + 1 h buffer = 25 hours
      put("Fondaparinux", 25 * DateTimeConstants.MILLIS_PER_HOUR);
      /* Administered within 3 days + INR > 1.5 I would like to base on the "duration" of 2-5 days
       * given variability in ordering, potential for held doses, etc. the INR where you would give
       * ppx once warfarin was held is not well defined, but 1.5 is reasonable and also our cutoff
       * for the lab value itself. */
      put("Warfarin", 3 * DateTimeConstants.MILLIS_PER_DAY);
    }
  };

  /**
   * Determines if the drug has been taken within its effectiveness period.
   *
   * @param timeTaken The time the drug was administered.
   * @param period The period, in seconds, that the drug is active.
   * @return Whether the time taken is within the period of efficacy for the drug to now.
   */
  public static boolean withinDrugPeriod(Date timeTaken, int period) {
    long timeTakenLong = timeTaken.getTime();
    Date lastEffectiveTime = new Date(timeTakenLong + (1000l * period));

    Date now = Calendar.getInstance().getTime();

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
    String code = "PLT";
    List<Observation> pltObservations = client.getObservationClient().searchObservation(encounterId,
        code, null);
    Observation freshestPltObservation = ObservationUtils.findFreshestObservation(pltObservations);
    IDatatype quantity = freshestPltObservation.getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(FIFTY_THOUSAND) < 0;
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
    String code = "INR";
    List<Observation> pltObservations = client.getObservationClient().searchObservation(encounterId,
        code, null);
    Observation freshestPltObservation = ObservationUtils.findFreshestObservation(pltObservations);
    IDatatype quantity = freshestPltObservation.getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(ONE_POINT_FIVE) > 0;
    } else {
      throw new NumberFormatException("Observation value is not of type QuantityDt.");
    }
  }

  /**
   * aPTT Ratio >1.3 Implementation
   *
   * @param client
   *     API client.
   * @param encounterId
   *     Relevant encounter ID.
   * @return true if conditions are met
   */
  public static boolean aPttRatioOver1point3(ClientBuilder client, String encounterId) {
    String code = "PTT";
    List<Observation> pltObservations = client.getObservationClient().searchObservation(encounterId,
        code, null);
    Observation freshestPltObservation = ObservationUtils.findFreshestObservation(pltObservations);
    IDatatype quantity = freshestPltObservation.getValue();
    if (quantity instanceof QuantityDt) {
      return ((QuantityDt) quantity).getValue().compareTo(ONE_POINT_THREE) > 0;
    } else {
      throw new NumberFormatException("Observation value is not of type QuantityDt.");
    }
  }
}
