// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.common.inject.Injectors;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.Periods;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Determines if sequential compression devices are in use.
 */
public class SCDsInUse {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to SCDS In Use.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to SCDS In Use.
   */
  public static boolean isRelevant(Observation observation) {
    Clock clock = Injectors.getInjector().getInstance(Clock.class);
    // Nurse shifts are 12 hours, so the lookback needs to be 24.
    Date twentyFourHoursAgo = Date.from(clock.instant().minus(24L, ChronoUnit.HOURS));
    return ObservationUtils.isAfter(observation, twentyFourHoursAgo) &&
      (ObservationCodeEnum.MECHANICAL_PPX_DEVICES.isCodeEquals(observation.getCode()) ||
      ObservationCodeEnum.MECHANICAL_PPX_INTERVENTIONS.isCodeEquals(observation.getCode()));
  }

  /**
   * Determines if sequential compression devices are in use.
   *
   * @param encounterId
   *     encounter to check.
   * @return true if SCDs are in use
   */
  public boolean isSCDsInUse(String encounterId) {
    PeriodDt fromCurrentOrPriorShift = Periods.getCurrentOrPriorShiftToNow(clock);
    Instant effectiveLower = fromCurrentOrPriorShift.getStart().toInstant();
    Instant effectiveUpper = fromCurrentOrPriorShift.getEnd().toInstant();

    Observations observations = apiClient.getObservationClient().list(encounterId);

    Optional<Observation> freshestDeviceFromShift = observations.findFreshest(
        ObservationCodeEnum.MECHANICAL_PPX_DEVICES.getCode(),
        effectiveLower,
        effectiveUpper);
    Optional<String> freshestDeviceValue = freshestDeviceFromShift
        .map(observation -> observation.getValue().toString());

    Optional<Observation> freshestInterventionFromShift = observations.findFreshest(
        ObservationCodeEnum.MECHANICAL_PPX_INTERVENTIONS.getCode(),
        effectiveLower,
        effectiveUpper);
    Optional<String> freshestInterventionValue = freshestInterventionFromShift
        .map(observation -> observation.getValue().toString());

    return freshestInterventionValue.isPresent() && freshestDeviceValue.isPresent()
        && freshestDeviceValue.get().contains("Sequential compression device(s) (SCDs)")
        && (freshestInterventionValue.get().contains("On left lower extremity")
        || freshestInterventionValue.get().contains("On right lower extremity"));
  }
}
