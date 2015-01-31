// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.urn;

import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Unit tests for URN utilities
 */
public class URNFactoryTest {
  @Test
  public void oneParam() {
    URI dfId = URNFactory.institutionPatientId("inst-id", "", "");
    assertEquals(dfId.toString(), "urn:df-institution-patientId-1:inst-id::");
  }

  @Test
  public void twoParams() {
    URI dfId = URNFactory.institutionPatientId("inst-id", "", "pat-id");
    assertEquals(dfId.toString(), "urn:df-institution-patientId-1:inst-id::pat-id");
  }

  @Test
  public void patientId() {
    URI dfId = URNFactory.patientId("23434:342342");
    assertEquals(dfId.toString(), "urn:df-patientId-1:23434%3A342342");
  }

  @Test
  public void allParams() {
    URI dfId = URNFactory.institutionPatientId("inst-id", "fac-id", "pat-id");
    assertEquals(dfId.toString(), "urn:df-institution-patientId-1:inst-id:fac-id:pat-id");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void nullParam() {
    URNFactory.institutionPatientId(null, "fac-id", "pat-id");
  }
}
