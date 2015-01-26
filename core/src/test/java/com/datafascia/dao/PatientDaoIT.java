// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.dao.PatientDao;
import com.datafascia.models.Patient;
import java.io.IOException;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration test for Patient DAO
 */
@Slf4j
public class PatientDaoIT extends DaoIT {
  @Test
  public void patients() throws AccumuloException, AccumuloSecurityException, IOException,
      TableExistsException, TableNotFoundException, InterruptedException {
    log.info("Getting the patients");
    Iterator<Patient> patients = PatientDao.patients(connect);

    int count = 0;
    while (patients.hasNext()) {
      Patient patient = patients.next();
      count++;
    }

    assertEquals(count, 703);
  }
}
