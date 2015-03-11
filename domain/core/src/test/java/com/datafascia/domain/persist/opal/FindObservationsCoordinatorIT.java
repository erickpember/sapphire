// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist.opal;

import com.datafascia.models.Observation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * {@link com.datafascia.domain.persist.opal.FindObservationsCoordinator} integration test
 */
@Slf4j
public class FindObservationsCoordinatorIT extends DaoIT {

  private static final String PATIENT_ID = "96087004";

  @Test
  public void should_find_observations() throws Exception {
    PatientDao patientDao = new PatientDao(accumuloTemplate);
    FindObservationsCoordinator findObservationsCoordinator = new FindObservationsCoordinator(
        patientDao, new EncounterDao(accumuloTemplate), new ObservationDao(accumuloTemplate));

    List<String> patientIds = patientDao.getPatientIds(true);
    for (String patientId : patientIds) {
      Collection<Observation> observations =
          findObservationsCoordinator.findObservationsByPatientId(patientId, Optional.empty());
      if (!observations.isEmpty()) {
        log.debug("patientId [{}]", patientId);
      }
    }
  }
}
