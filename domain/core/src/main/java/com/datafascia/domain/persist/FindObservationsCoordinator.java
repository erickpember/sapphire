// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Observation;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * Coordinates steps to find observations.
 */
@Singleton @Slf4j
public class FindObservationsCoordinator {

  private final EncounterRepository encounterRepository;
  private final ObservationRepository observationRepository;

  /**
   * Construct co-ordinator with the data access objects
   *
   * @param encounterRepository the encounter data access object
   * @param observationRepository the observation data access object
   */
  @Inject
  public FindObservationsCoordinator(
      EncounterRepository encounterRepository, ObservationRepository observationRepository) {

    this.encounterRepository = encounterRepository;
    this.observationRepository = observationRepository;
  }

  /**
   * Finds observations for the patient, optionally filtered to capture times in a time interval.
   *
   * @param patientId
   *     patient identifier
   * @param captureTimeInterval
   *     capture time interval to include
   * @return collection of observations, empty if none found
   */
  public List<Observation> findObservationsByPatientId(
      Id<UnitedStatesPatient> patientId, Optional<Interval<Instant>> captureTimeInterval) {

    Instant startIssued = null;
    Instant endIssued = null;
    if (captureTimeInterval.isPresent()) {
      startIssued = Instant.from(captureTimeInterval.get().getStartInclusive());
      endIssued = Instant.from(captureTimeInterval.get().getEndExclusive());
    }

    List<Observation> foundObservations = new ArrayList<>();
    List<Encounter> encounters = encounterRepository.list(patientId);
    for (Encounter encounter : encounters) {
      List<Observation> candidateObservations =
          observationRepository.list(patientId, encounter.getId());
      for (Observation observation : candidateObservations) {
        if (startIssued != null
            && (observation.getIssued().isBefore(startIssued)
            || !observation.getIssued().isBefore(endIssued))) {
          continue;
        }
        foundObservations.add(observation);
      }
    }

    return foundObservations;
  }
}
