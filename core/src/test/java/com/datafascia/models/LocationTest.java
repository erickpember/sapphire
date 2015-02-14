// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for location model.
 */
public class LocationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testLocation() throws IOException, URISyntaxException {
    Location decoded = (Location) geneticEncodeDecodeTest(TestModels.location);
    assertEquals(decoded.getLocation(), TestModels.getURI());
    assertEquals(decoded.getPeriod(), TestModels.period);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("location");
    jsonProperties.add("period");
    geneticJsonContainsFieldsTest(TestModels.location, jsonProperties);
  }
}
