// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for quantity model.
 */
public class QuantityTest extends ModelTestBase {
  @Test
  public <T extends Object> void testQuantity() throws IOException, URISyntaxException {
    Quantity decoded = (Quantity) geneticEncodeDecodeTest(TestModels.quantity);
    assertEquals(decoded.getValue(), new BigDecimal(10));
    assertEquals(decoded.getComparator(), QuantityComparator.GreaterThan);
    assertEquals(decoded.getUnits(), "seconds");
    assertEquals(decoded.getSystem(), TestModels.getURI());
    assertEquals(decoded.getCode(), TestModels.codeable);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("value");
    jsonProperties.add("Comparator");
    jsonProperties.add("units");
    jsonProperties.add("system");
    jsonProperties.add("code");
    geneticJsonContainsFieldsTest(TestModels.quantity, jsonProperties);
  }
}
