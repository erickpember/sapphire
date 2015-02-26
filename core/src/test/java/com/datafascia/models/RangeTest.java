// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;
import tec.units.ri.util.UCUM;

import static org.testng.Assert.assertEquals;

/**
 * Test code for range model.
 */
public class RangeTest extends ModelTestBase {
  @Test
  public <T extends Object> void testRange() throws IOException, URISyntaxException {
    Range decoded = (Range) geneticEncodeDecodeTest(TestModels.range);
    assertEquals(decoded.getLow(), new NumericQuantity(new BigDecimal(0), UCUM.LITER));
    assertEquals(decoded.getHigh(), new NumericQuantity(new BigDecimal(28935.78394d), UCUM.LITER));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("low");
    jsonProperties.add("high");
    geneticJsonContainsFieldsTest(TestModels.range, jsonProperties);
  }
}
