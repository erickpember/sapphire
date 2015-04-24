// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Schedule model.
 */
public class ScheduleTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSchedule() throws IOException, URISyntaxException {
    Schedule decoded = (Schedule) geneticEncodeDecodeTest(TestModels.schedule);

    assertEquals(decoded.getRepeatDuration(), new BigDecimal(9001));
    assertEquals(decoded.getRepeatEnd(), TestModels.getDateTime());
    assertEquals(decoded.getRepeatCount(), (Integer) 9001);
    assertEquals(decoded.getRepeatFrequency(), (Integer) 2600);
    assertEquals(decoded.getEvent(), TestModels.period);
    assertEquals(decoded.getRepeatWhen(), ScheduleEventType.WAKE);
    assertEquals(decoded.getRepeatUnits(), ScheduleTimeUnit.WK);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("event");
    jsonProperties.add("repeatCount");
    jsonProperties.add("repeatDuration");
    jsonProperties.add("repeatEnd");
    jsonProperties.add("repeatFrequency");
    jsonProperties.add("repeatUnits");
    jsonProperties.add("repeatWhen");

    geneticJsonContainsFieldsTest(TestModels.schedule, jsonProperties);
  }
}
