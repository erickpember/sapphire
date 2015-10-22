// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.List;
import javax.inject.Inject;

/**
 * Utilities related to benzodiazepine avoidance.
 */
public class BenzodiazepineAvoidance {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Determines whether benzodiazepine avoidance is contraindicated.
   *
   * @param encounterId The encounter to check.
   * @return Whether benzodiazepine avoidance is contraindicated.
   */
  public boolean isBenzodiazepineAvoidanceContraindicated(String encounterId) {
    List<Observation> observations = apiClient.getObservationClient().searchObservation(encounterId,
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
