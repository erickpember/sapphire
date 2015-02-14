// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for period model.
 */
public class PeriodTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPeriod() throws IOException, URISyntaxException {
    Period decoded = (Period) geneticEncodeDecodeTest(TestModels.period);
    assertEquals(decoded.getStart(), TestModels.getDate());
    assertEquals(decoded.getStop(), TestModels.getDate());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("start");
    jsonProperties.add("stop");
    geneticJsonContainsFieldsTest(TestModels.period, jsonProperties);
  }
}
