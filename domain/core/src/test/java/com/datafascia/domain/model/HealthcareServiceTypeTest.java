// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for HealthcareServiceType model.
 */
public class HealthcareServiceTypeTest extends ModelTestBase {
  @Test
  public <T extends Object> void testHealthcareServiceType() throws IOException,
      URISyntaxException {
    HealthcareServiceType decoded
        = (HealthcareServiceType) geneticEncodeDecodeTest(
            TestModels.healthcareServiceType);

    assertEquals(decoded.getSpecialties(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getType(), TestModels.codeable);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("specialties");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.healthcareServiceType, jsonProperties);
  }
}
