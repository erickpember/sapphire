// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Schedule model.
 */
public class TimingTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSchedule() throws IOException, URISyntaxException {
    Timing decoded = (Timing) geneticEncodeDecodeTest(TestModels.timing);

    assertEquals(decoded.getRepeatDuration(), new BigDecimal(9001));
    assertEquals(decoded.getRepeatFrequency(), new BigDecimal(9001));
    assertEquals(decoded.getRepeatFrequencyMax(), new BigDecimal(9001));
    assertEquals(decoded.getRepeatPeriod(), new BigDecimal(9001));
    assertEquals(decoded.getRepeatPeriodMax(), new BigDecimal(9001));
    assertEquals(decoded.getRepeatEnd(), TestModels.getDateTime());
    assertEquals(decoded.getRepeatCount(), (Integer) 0);
    assertEquals(decoded.getRepeatBounds(), TestModels.period);
    assertEquals(decoded.getEvents(), Arrays.asList(TestModels.getDateTime()));
    assertEquals(decoded.getRepeatWhen(), TimingEventType.AC);
    assertEquals(decoded.getRepeatDurationUnits(), TimingTimeUnit.D);
    assertEquals(decoded.getRepeatPeriodUnits(), TimingTimeUnit.D);
    assertEquals(decoded.getRepeatUnits(), TimingTimeUnit.D);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("events");
    jsonProperties.add("repeatBounds");
    jsonProperties.add("repeatCount");
    jsonProperties.add("repeatDuration");
    jsonProperties.add("repeatDurationUnits");
    jsonProperties.add("repeatEnd");
    jsonProperties.add("repeatFrequency");
    jsonProperties.add("repeatFrequencyMax");
    jsonProperties.add("repeatPeriod");
    jsonProperties.add("repeatPeriodMax");
    jsonProperties.add("repeatPeriodUnits");
    jsonProperties.add("repeatUnits");
    jsonProperties.add("repeatWhen");

    geneticJsonContainsFieldsTest(TestModels.timing, jsonProperties);
  }
}
