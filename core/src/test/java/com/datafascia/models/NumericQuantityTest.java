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
 * Test code for NumericQuantity model.
 */
public class NumericQuantityTest extends ModelTestBase {
  @Test
  public <T extends Object> void testNumericQuantity() throws IOException, URISyntaxException {
    NumericQuantity decoded = (NumericQuantity) geneticEncodeDecodeTest(TestModels.numericQuantity);
    assertEquals(decoded.getValue(), new BigDecimal("3.1"));
    assertEquals(decoded.getUnit(), UCUM.METER);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("value");
    jsonProperties.add("unit");
    geneticJsonContainsFieldsTest(TestModels.numericQuantity, jsonProperties);
  }
}
