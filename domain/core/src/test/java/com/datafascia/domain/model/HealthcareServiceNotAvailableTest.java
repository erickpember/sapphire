// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for HealthcareServiceNotAvailable model.
 */
public class HealthcareServiceNotAvailableTest extends ModelTestBase {
  @Test
  public <T extends Object> void testHealthcareServiceNotAvailable() throws IOException,
      URISyntaxException {
    HealthcareServiceNotAvailable decoded
        = (HealthcareServiceNotAvailable) geneticEncodeDecodeTest(
            TestModels.healthcareServiceNotAvailable);

    assertEquals(decoded.getDescription(), "lunch break");
    assertEquals(decoded.getDuring(), TestModels.period);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("description");
    jsonProperties.add("during");

    geneticJsonContainsFieldsTest(TestModels.healthcareServiceNotAvailable, jsonProperties);
  }
}
