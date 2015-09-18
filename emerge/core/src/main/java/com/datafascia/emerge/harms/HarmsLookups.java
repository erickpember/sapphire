// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.math.BigDecimal;
import java.util.List;

/**
 * A collection of simple API lookups commonly used as components in harms logic.
 */
public class HarmsLookups {
  private static final BigDecimal ONE_POINT_THREE = new BigDecimal("1.3");
  private static final BigDecimal ONE_POINT_FIVE = new BigDecimal("1.5");
  private static final BigDecimal FIFTY_THOUSAND = new BigDecimal("50000");

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
