// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test code for HealthcareServiceAvailableTime model.
 */
public class HealthcareServiceAvailableTimeTest extends ModelTestBase {
  @Test
  public <T extends Object> void testHealthcareServiceAvailableTime() throws IOException,
      URISyntaxException {
    HealthcareServiceAvailableTime decoded
        = (HealthcareServiceAvailableTime) geneticEncodeDecodeTest(
            TestModels.healthcareServiceAvailableTime);

    assertTrue(decoded.getAllDay());
    assertEquals(decoded.getAvailableEndTime(), TestModels.duration);
    assertEquals(decoded.getAvailableStartTime(), TestModels.duration);
    assertEquals(decoded.getDaysOfWeek(), Arrays.asList(DayOfWeek.SUNDAY));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("allDay");
    jsonProperties.add("availableEndTime");
    jsonProperties.add("availableStartTime");
    jsonProperties.add("daysOfWeek");

    geneticJsonContainsFieldsTest(TestModels.healthcareServiceAvailableTime, jsonProperties);
  }
}
