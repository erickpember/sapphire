// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Range model.
 */
public class RangeTest extends ModelTestBase {
  @Test
  public <T extends Object> void testRange() throws IOException, URISyntaxException {
    Range decoded = (Range) geneticEncodeDecodeTest(TestModels.range);

    assertEquals(decoded.getLow(), TestModels.numericQuantity);
    assertEquals(decoded.getHigh(), TestModels.numericQuantity);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("high");
    jsonProperties.add("low");

    geneticJsonContainsFieldsTest(TestModels.range, jsonProperties);
  }
}
