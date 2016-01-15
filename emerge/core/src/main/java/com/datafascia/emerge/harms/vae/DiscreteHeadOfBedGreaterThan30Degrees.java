// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.Periods;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.MaybeEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * VAE head of bed greater than or equal to 30 degrees implementation
 */
@Slf4j
public class DiscreteHeadOfBedGreaterThan30Degrees {

  private static final Set<String> CONTRAINDICATED_BED_ORDER_CODES = ImmutableSet.of(
      ProcedureRequestCodeEnum.HOB_FLAT.getCode(),
      ProcedureRequestCodeEnum.PRONE.getCode(),
      ProcedureRequestCodeEnum.SUPINE.getCode(),
      ProcedureRequestCodeEnum.LIE_FLAT.getCode(),
      ProcedureRequestCodeEnum.HOB_LESS_THAN_10.getCode(),
      ProcedureRequestCodeEnum.HOB_LESS_THAN_30.getCode(),
      ProcedureRequestCodeEnum.BED_REST_HOB_LESS_THAN_31.getCode(),
      ProcedureRequestCodeEnum.BED_REST_HOB_FLAT.getCode());

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to head of bed greater than or equal to 30 degrees.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to head of bed greater than or equal to 30 degrees
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.HEAD_OF_BED.isCodeEquals(observation.getCode());
  }

  /**
   * Checks if the head of bed angle is greater than or equal to 30 degrees.
   *
   * @param encounterId
   *     encounter to check
   * @return "Yes", "No", "Not Documented" or "Contraindicated"
   */
  public MaybeEnum getHeadOfBedGreaterThan30Degrees(String encounterId) {
    Instant now = Instant.now(clock);

    boolean contraindicated = apiClient.getProcedureRequestClient()
        .search(encounterId)
        .stream()
        .filter(request ->
            CONTRAINDICATED_BED_ORDER_CODES.contains(
                request.getCode().getCodingFirstRep().getCode()))
        .anyMatch(request -> ProcedureRequestUtils.isCurrent(request, Date.from(now)));
    if (contraindicated) {
      return MaybeEnum.CONTRAINDICATED;
    }

    PeriodDt fromCurrentOrPriorShift = Periods.getCurrentOrPriorShiftToNow(clock);
    Instant effectiveLower = fromCurrentOrPriorShift.getStart().toInstant();
    Instant effectiveUpper = fromCurrentOrPriorShift.getEnd().toInstant();

    Observations observations = apiClient.getObservationClient().list(encounterId);

    Optional<Observation> freshestDiscreteHOB = observations.findFreshest(
        ObservationCodeEnum.HEAD_OF_BED.getCode(),
        effectiveLower,
        effectiveUpper);

    if (freshestDiscreteHOB.isPresent()) {
      String value = freshestDiscreteHOB.get().getValue().toString();
      switch (value) {
        case "HOB 30":
        case "HOB 45":
        case "HOB 60":
        case "HOB 90":
          return MaybeEnum.YES;
        case "HOB Flat":
        case "HOB less than 20":
          return MaybeEnum.NO;
        case "Self regulated":
          return MaybeEnum.NOT_DOCUMENTED;
        default:
          log.warn("Unexpected head of bed value [{}]", value);
      }
    }

    return MaybeEnum.NO;
  }
}
