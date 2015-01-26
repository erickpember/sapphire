// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.models.Observation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * {@link ObservationDao} integration test
 */
@Slf4j
public class ObservationDaoIT extends DaoIT {

  private static final String PATIENT_ID = "96087004";
  private static final String AUTHORIZATIONS = "System";

  @Test
  public void should_find_observations() throws Exception {
    ArrayList<String> patientIds = PatientDao.getPatientIds(connect, "true");
    ObservationDao observationDao = new ObservationDao(connect);
    for (String patientId : patientIds) {
      Collection<Observation> observations = observationDao.findObservationsByPatientId(
          patientId, Optional.empty(), AUTHORIZATIONS);
      if (!observations.isEmpty()) {
        log.debug("patientId [{}]", patientId);
      }
    }
  }
}
