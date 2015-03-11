// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.urn;

import com.datafascia.common.urn.annotations.IdNamespace;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests for URN maps
 */
@Slf4j
public class URNMapTest {
  @BeforeSuite
  public void setup() {
    // Load the mappings by scanning the package
    URNMap.idNSMapping(getClass().getPackage().getName());
  }

  @Test
  public void idTest() {
    assertEquals(URNMap.getIdNamespace(Observation.class.getName()), URNFactory.NS_OBSERVATION_ID);
    assertEquals(URNMap.getIdNamespace(Patient.class.getName()), URNFactory.NS_PATIENT_ID);
  }

  @Test
  public void nsTest() {
    assertEquals(URNMap.getClassFromIdNamespace(URNFactory.NS_OBSERVATION_ID), Observation.class);
    assertEquals(URNMap.getClassFromIdNamespace(URNFactory.NS_PATIENT_ID), Patient.class);
  }
}

@IdNamespace(URNFactory.NS_OBSERVATION_ID)
class Observation {
}

@IdNamespace(URNFactory.NS_PATIENT_ID)
class Patient {
}
