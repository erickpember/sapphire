// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.codahale.metrics.Timer;
import com.datafascia.accumulo.QueryTemplate;
import com.datafascia.models.CodeableConcept;
import com.datafascia.models.Observation;
import com.datafascia.models.ObservationValue;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

/**
 * Observation data access.
 */
@Singleton @Slf4j
public class ObservationDao extends OpalDao {

  @Inject
  public ObservationDao(QueryTemplate queryTemplate) {
    super(queryTemplate);
  }

  /**
   * Finds observations for the update.
   *
   * @param updateId
   *     update identifier
   * @return collection of observations, empty if none found
   */
  public List<Observation> findObservations(String updateId) {
    Timer.Context timerContext = queryTemplate.getTimerContext(getClass(), "findObservations");
    try {
      List<Observation> observations = new ArrayList<>();
      Date issued = null;

      Scanner scanner = getScanner();
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
    } finally {
      timerContext.stop();
    }
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
