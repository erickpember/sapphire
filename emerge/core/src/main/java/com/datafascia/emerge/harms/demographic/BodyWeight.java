// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.demographic;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;

/**
 * Gets patient's body height and weight.
 */
public class BodyWeight {

  private static final Set<String> RELEVANT_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.ADMISSION_WEIGHT.getCode(),
      ObservationCodeEnum.CLINICAL_WEIGHT.getCode());

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to body weight.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to body weight
   */
  public static boolean isRelevant(Observation observation) {
    return RELEVANT_OBSERVATION_CODES.contains(observation.getCode().getCodingFirstRep().getCode());
  }

  private static BigDecimal getValue(Observation observation) {
    QuantityDt quantity = (QuantityDt) observation.getValue();
    switch (quantity.getUnit()) {
      case "kg":
        return quantity.getValue();
      default:
        throw new IllegalStateException("Unknown unit of measure " + quantity.getUnit());
    }
  }

  /**
   * Gets patient's body weight.
   *
   * @param encounterId
   *     encounter to search
   * @return weight in kg, or {@code null} if not found
   */
  public BigDecimal apply(String encounterId) {
    Observations observations = apiClient.getObservationClient().list(encounterId);

    Optional<Observation> clinicalWeight = observations.findFreshest(
        ObservationCodeEnum.CLINICAL_WEIGHT.getCode());
    if (clinicalWeight.isPresent()) {
      return getValue(clinicalWeight.get());
    }

    return observations.findFreshest(ObservationCodeEnum.ADMISSION_WEIGHT.getCode())
        .map(observation -> getValue(observation))
        .orElse(null);
  }
}
