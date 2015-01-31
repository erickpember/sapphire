// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.urn;

import com.datafascia.reflections.PackageUtils;
import com.datafascia.urn.annotations.IDNamespace;
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
    URNMap.idNSMapping("com.datafascia.urn");
  }

  @Test
  public void idTest() {
    assertEquals(URNMap.getIDNamespace(Observation.class.getName()), URNFactory.NS_OBSERVATION_ID);
    assertEquals(URNMap.getIDNamespace(Patient.class.getName()), URNFactory.NS_PATIENT_ID);
  }

  @Test
  public void nsTest() {
    assertEquals(URNMap.getClassFromIDNamespace(URNFactory.NS_OBSERVATION_ID), Observation.class);
    assertEquals(URNMap.getClassFromIDNamespace(URNFactory.NS_PATIENT_ID), Patient.class);
  }
}

@IDNamespace(URNFactory.NS_OBSERVATION_ID)
class Observation {
}

@IDNamespace(URNFactory.NS_PATIENT_ID)
class Patient {
}
