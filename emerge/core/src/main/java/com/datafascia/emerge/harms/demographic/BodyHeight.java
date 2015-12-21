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
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.util.SI;
import tec.units.ri.util.SIPrefix;

/**
 * Gets patient's body height and weight.
 */
public class BodyHeight {

  private static final Set<String> RELEVANT_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.ADMISSION_HEIGHT.getCode(),
      ObservationCodeEnum.CLINICAL_HEIGHT.getCode());
  private static final Unit<Length> CENTIMETRE = SIPrefix.CENTI(SI.METRE);
  private static final Unit<Length> INCH = CENTIMETRE.multiply(100).divide(254);
  private static final Unit<Length> FOOT = INCH.divide(12);

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to body height.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to body height
   */
  public static boolean isRelevant(Observation observation) {
    return RELEVANT_OBSERVATION_CODES.contains(observation.getCode().getCodingFirstRep().getCode());
  }

  private static Quantity<Length> getQuantityLength(Observation observation) {
    QuantityDt fromQuantity = (QuantityDt) observation.getValue();

    Unit<Length> unit;
    switch (fromQuantity.getUnit()) {
      case "cm":
        unit = CENTIMETRE;
        break;
      case "ft":
        unit = FOOT;
        break;
      case "in":
        unit = INCH;
        break;
      default:
        throw new IllegalStateException("Unknown unit of measure " + fromQuantity.getUnit());
    }

    return Quantities.getQuantity(fromQuantity.getValue(), unit);
  }

  private static BigDecimal getValue(Observation observation) {
    Quantity<Length> quantity = getQuantityLength(observation);
    return BigDecimal.valueOf(quantity.to(CENTIMETRE).getValue().doubleValue())
        .setScale(0, BigDecimal.ROUND_HALF_UP);
  }

  /**
   * Gets patient's body height.
   *
   * @param encounterId
   *     encounter to search
   * @return height in cm, or {@code null} if not found
   */
  public BigDecimal apply(String encounterId) {
    Observations observations = apiClient.getObservationClient().list(encounterId);

    Optional<Observation> clinicalHeight = observations.findFreshest(
        ObservationCodeEnum.CLINICAL_HEIGHT.getCode());
    if (clinicalHeight.isPresent()) {
      return getValue(clinicalHeight.get());
    }

    return observations.findFreshest(ObservationCodeEnum.ADMISSION_HEIGHT.getCode())
        .map(observation -> getValue(observation))
        .orElse(null);
  }
}
