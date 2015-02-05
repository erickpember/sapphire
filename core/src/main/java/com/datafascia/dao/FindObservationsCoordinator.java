// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.common.time.Interval;
import com.datafascia.models.Observation;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Coordinates steps to find observations.
 */
@Singleton @Slf4j
public class FindObservationsCoordinator {

  private PatientDao patientDao;
  private EncounterDao encounterDao;
  private ObservationDao observationDao;

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
      String patientId, Optional<Interval<Instant>> captureTimeInterval)
  {
    Date startIssued = null;
    Date endIssued = null;
    if (captureTimeInterval.isPresent()) {
      startIssued = Date.from(captureTimeInterval.get().getStartInclusive());
      endIssued = Date.from(captureTimeInterval.get().getEndExclusive());
    }

    Collection<Observation> foundObservations = new ArrayList<>();
    List<String> visitIds = patientDao.findVisitIds(patientId);
    for (String visitId : visitIds) {
      List<String> updateIds = encounterDao.findUpdateIds(visitId);
      for (String updateId : updateIds) {
        List<Observation> candidateObservations =
            observationDao.findObservations(updateId);
        for (Observation observation : candidateObservations) {
          if (startIssued != null &&
              (observation.getIssued().before(startIssued) ||
               !observation.getIssued().before(endIssued))) {
            continue;
          }
          foundObservations.add(observation);
        }
      }
    }

    return foundObservations;
  }
}
