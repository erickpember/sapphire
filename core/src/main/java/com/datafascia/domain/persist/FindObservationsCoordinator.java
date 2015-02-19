// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.time.Interval;
import com.datafascia.models.Observation;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
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

  private final PatientDao patientDao;
  private final EncounterDao encounterDao;
  private final ObservationDao observationDao;

  /**
   * Construct co-ordinator with the data access objects
   *
   * @param patientDao the patient data access object
   * @param encounterDao the encounter data access object
   * @param observationDao the observation data access object
   */
  @Inject
  public FindObservationsCoordinator(
      PatientDao patientDao, EncounterDao encounterDao, ObservationDao observationDao) {

    this.patientDao = patientDao;
    this.encounterDao = encounterDao;
    this.observationDao = observationDao;
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
  public Collection<Observation> findObservationsByPatientId(
      String patientId, Optional<Interval<Instant>> captureTimeInterval) {
    Instant startIssued = null;
    Instant endIssued = null;
    if (captureTimeInterval.isPresent()) {
      startIssued = Instant.from(captureTimeInterval.get().getStartInclusive());
      endIssued = Instant.from(captureTimeInterval.get().getEndExclusive());
    }

    Collection<Observation> foundObservations = new ArrayList<>();
    List<String> visitIds = patientDao.findVisitIds(patientId);
    for (String visitId : visitIds) {
      List<String> updateIds = encounterDao.findUpdateIds(visitId);
      for (String updateId : updateIds) {
        List<Observation> candidateObservations =
            observationDao.findObservations(updateId);
        for (Observation observation : candidateObservations) {
          if (startIssued != null
              && (observation.getIssued().isBefore(startIssued)
              || !observation.getIssued().isBefore(endIssued))) {
            continue;
          }
          foundObservations.add(observation);
        }
      }
    }

    return foundObservations;
  }
}
