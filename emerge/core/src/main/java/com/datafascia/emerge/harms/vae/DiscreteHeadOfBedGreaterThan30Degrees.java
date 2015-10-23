// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.MaybeEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
      "HOB Flat",
      "Prone",
      "Supine",
      "Lie Flat",
      "HOB <10",
      "HOB <30",
      "Bed Rest with HOB <=30 Degrees",
      "Bed Rest with HOB Flat");

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
    return ObservationCodeEnum.HEAD_OF_BED.getCode().equals(
        observation.getCode().getCodingFirstRep().getCode());
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
    Date thirteenHoursAgo = Date.from(now.minus(13, ChronoUnit.HOURS));

    Optional<Observation> freshestDiscreteHOB = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.HEAD_OF_BED.getCode(), thirteenHoursAgo);
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
        default:
          log.warn("Unexpected head of bed value [{}]", value);
      }
    }

    boolean contraindicated = apiClient.getProcedureRequestClient()
        .getProcedureRequest(encounterId)
        .stream()
        .filter(request ->
            CONTRAINDICATED_BED_ORDER_CODES.contains(
                request.getCode().getCodingFirstRep().getCode()))
        .anyMatch(request -> ProcedureRequestUtils.beforeNow(request));
    if (contraindicated) {
      return MaybeEnum.CONTRAINDICATED;
    }

    return MaybeEnum.NOT_DOCUMENTED;
  }
}
