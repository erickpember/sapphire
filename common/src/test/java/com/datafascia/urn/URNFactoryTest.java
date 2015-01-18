// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.urn;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
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
    try {
      URI dfId = URNFactory.getInstitutionPatientId("inst-id", "", "");
      assertEquals(dfId.toString(), "urn:df-institution-patientId-1:inst-id::");
    } catch (Exception e) {
      fail("Exception constructing URN not expected " + e.getMessage());
    }
  }

  @Test
  public void twoParams() {
    try {
      URI dfId = URNFactory.getInstitutionPatientId("inst-id", "", "pat-id");
      assertEquals(dfId.toString(), "urn:df-institution-patientId-1:inst-id::pat-id");
    } catch (Exception e) {
      fail("Exception constructing URN not expected " + e.getMessage());
    }
  }

  @Test
  public void patientId() {
    try {
      URI dfId = URNFactory.patientId("23434:342342");
      assertEquals(dfId.toString(), "urn:df-patientId-1:23434%253A342342");
    } catch (Exception e) {
      fail("Exception constructing URN not expected " + e.getMessage());
    }
  }

  @Test
  public void allParams() {
    try {
      URI dfId = URNFactory.getInstitutionPatientId("inst-id", "fac-id", "pat-id");
      assertEquals(dfId.toString(), "urn:df-institution-patientId-1:inst-id:fac-id:pat-id");
    } catch (Exception e) {
      fail("Exception constructing URN not expected " + e.getMessage());
    }
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void nullParam() {
    try {
      URNFactory.getInstitutionPatientId(null, "fac-id", "pat-id");
    } catch (URISyntaxException | UnsupportedEncodingException e) {
      fail("Exception constructing URN not expected " + e.getMessage());
    }
  }
}
