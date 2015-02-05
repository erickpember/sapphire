// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import static org.testng.Assert.assertEquals;

import com.datafascia.models.Patient;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * Integration test for Patient DAO
 */
@Slf4j
public class PatientDaoIT extends DaoIT {
  @Test
  public void patients() throws Exception {
    log.info("Getting the patients");
    PatientDao patientDao = new PatientDao(queryTemplate);
    Iterator<Patient> patients = patientDao.patients(true);

    int count = 0;
    while (patients.hasNext()) {
      Patient patient = patients.next();
      count++;
    }

    assertEquals(count, 703);
  }
}
