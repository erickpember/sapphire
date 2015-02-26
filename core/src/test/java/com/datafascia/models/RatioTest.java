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
 * Test code for ratio model.
 */
public class RatioTest extends ModelTestBase {
  @Test
  public <T extends Object> void testRatio() throws IOException, URISyntaxException {
    Ratio decoded = (Ratio) geneticEncodeDecodeTest(TestModels.ratio);
    assertEquals(decoded.getNumerator(), new NumericQuantity(new BigDecimal(1), UCUM.CELSIUS));
    assertEquals(decoded.getDenominator(), new NumericQuantity(new BigDecimal(3), UCUM.CELSIUS));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("numerator");
    jsonProperties.add("denominator");
    geneticJsonContainsFieldsTest(TestModels.ratio, jsonProperties);
  }
}
