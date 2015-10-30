// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.demographic;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationEffectiveDescendingComparator;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.util.List;
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

  private List<Observation> getObservations(String encounterId) {
    List<Observation> observations =
        apiClient.getObservationClient().searchObservation(encounterId, null, null);
    observations.sort(new ObservationEffectiveDescendingComparator());
    return observations;
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
    List<Observation> observations = getObservations(
        encounterId);

    Optional<BigDecimal> observedWeight = observations.stream()
        .filter(observation ->
            ObservationCodeEnum.CLINICAL_WEIGHT.isCodeEquals(observation.getCode()))
        .findFirst()
        .map(observation -> getValue(observation));
    if (observedWeight.isPresent()) {
      return observedWeight.get();
    }

    return observations.stream()
        .filter(observation ->
            ObservationCodeEnum.ADMISSION_WEIGHT.isCodeEquals(observation.getCode()))
        .findFirst()
        .map(observation -> getValue(observation))
        .orElse(null);
  }
}
