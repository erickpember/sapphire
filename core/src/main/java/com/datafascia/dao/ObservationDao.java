// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.models.CodeableConcept;
import com.datafascia.models.Observation;
import com.datafascia.models.ObservationValue;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Observation data access.
 */
@Singleton @Slf4j
public class ObservationDao extends OpalDao {

  @Inject
  public ObservationDao(Connector connector) {
    super(connector);
  }

  /**
   * Finds observations for the patient in the time range specified by the open interval
   * [startCaptureTime, endCaptureTime).  That is, each found item must have a time less than the
   * upper bound endpoint.
   *
   * @param patientId
   *     patient identifier
   * @param startCaptureTime
   *     minimum capture time to include
   * @param endCaptureTime
   *     upper bound of capture time to include
   * @param auths
   *     authorizations
   * @return collection of observations, empty if none found
   */
  public Collection<Observation> findObservationsByPatientId(
      String patientId, Instant startCaptureTime, Instant endCaptureTime, String auths)
  {
    Date startIssued = Date.from(startCaptureTime);
    Date endIssued = Date.from(endCaptureTime);
    Authorizations authorizations = new Authorizations(auths);

    Collection<Observation> foundObservations = new ArrayList<>();
    List<String> visitIds = findVisitIds(patientId, authorizations);
    for (String visitId : visitIds) {
      List<String> updateIds = findUpdateIds(visitId, authorizations);
      for (String updateId : updateIds) {
        List<Observation> candidateObservations = findObservations(updateId, authorizations);
        for (Observation observation : candidateObservations) {
          if (!observation.getIssued().before(startIssued) &&
              observation.getIssued().before(endIssued)) {
            foundObservations.add(observation);
          }
        }
      }
    }

    return foundObservations;
  }

  private List<String> findVisitIds(String patientId, Authorizations authorizations) {
    Optional<Value> value = getFieldValue(
        ObjectStore, Kinds.PATIENT_OBJECT, patientId, PatientFields.VISIT_OIIDS, authorizations);
    if (value.isPresent()) {
      return Arrays.asList(decodeStringArray(value.get()));
    } else {
      return Collections.emptyList();
    }
  }

  private List<String> findUpdateIds(String visitId, Authorizations authorizations) {
    Optional<Value> value = getFieldValue(
        ObjectStore, Kinds.PATIENT_VISIT_MAP, visitId, VisitFields.UPDATE_OIIDS, authorizations);
    if (value.isPresent()) {
      return Arrays.asList(decodeStringArray(value.get()));
    } else {
      return Collections.emptyList();
    }
  }

  private List<Observation> findObservations(String updateId, Authorizations authorizations) {
    List<Observation> observations = new ArrayList<>();
    Date issued = null;

    Scanner scanner = getScanner(authorizations);
    scanner.setRange(toRange(Kinds.PATIENT_UPDATE_MAP, updateId));
    for (Map.Entry<Key, Value> entry : scanner) {
      String fieldName = splitKey(entry.getKey().getColumnFamily().toString())[1];
      Optional<UpdateField> optionalField = UpdateField.ofFieldName(fieldName);
      if (!optionalField.isPresent()) {
        continue;
      }

      UpdateField field = optionalField.get();
      switch (field) {
        case CAPTURE_TIME:
          issued = decodeDate(entry.getValue());
          break;
        case NUMERICAL_PAIN_LEVEL_LOW:
        case NUMERICAL_PAIN_LEVEL_HIGH:
        case BEHAVIORAL_PAIN_LEVEL_LOW:
        case BEHAVIORAL_PAIN_LEVEL_HIGH:
        case RASS_PAIN_LEVEL_LOW:
        case RASS_PAIN_LEVEL_HIGH:
          observations.add(createObservation(field, entry.getValue()));
          break;
      }
    }

    for (Observation observation : observations) {
      observation.setIssued(issued);
    }

    return observations;
  }

  private Observation createObservation(UpdateField field, Value value) {
    ObservationValue observationValue = new ObservationValue();
    observationValue.setText(decodeString(value));

    Observation observation = new Observation();
    observation.setName(new CodeableConcept(field.getFieldName(), field.getDisplayName()));
    observation.setValues(observationValue);
    return observation;
  }
}
