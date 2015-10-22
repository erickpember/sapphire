// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.List;

/**
 * Utilities related to benzodiazepine avoidance.
 */
public class BenzodiazepineAvoidance {

  /**
   * Determines whether benzodiazepine avoidance is contraindicated.
   *
   * @param client The client to use.
   * @param encounterId The encounter to check.
   * @return Whether benzodiazepine avoidance is contraindicated.
   */
  public static boolean benzodiazepineAvoidanceContraindicated(ClientBuilder client,
      String encounterId) {
    List<Observation> observations = client.getObservationClient().searchObservation(encounterId,
        ObservationCodeEnum.BENZODIAZEPINE_AVOIDANCE.getCode(), null);

    for (Observation observation : observations) {
      if (observation.getValue() instanceof QuantityDt
          && ((QuantityDt) observation.getValue()).getValue().doubleValue() > 0) {
        return true;
      }
    }

    return false;
  }
}
