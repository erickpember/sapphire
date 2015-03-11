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
 * Test code for SampledData model.
 */
public class SampledDataTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSampledData() throws IOException, URISyntaxException {
    SampledData decoded = (SampledData) geneticEncodeDecodeTest(TestModels.sampledData);
    ArrayList<BigDecimal> data = new ArrayList<>();
    data.add(new BigDecimal(8447));
    data.add(new BigDecimal(958737));
    data.add(new BigDecimal(38382672));
    data.add(new BigDecimal(.000001));
    data.add(new BigDecimal(-900000000));
    assertEquals(decoded.getData(), data);
    assertEquals(decoded.getDimensions(), 9000l);
    assertEquals(decoded.getFactor(), new BigDecimal(5));
    assertEquals(decoded.getLowerLimit(), new BigDecimal(3.50));
    assertEquals(decoded.getOrigin(), new NumericQuantity(new BigDecimal(3.1), UCUM.BTU));
    assertEquals(decoded.getPeriod(), new BigDecimal(28));
    assertEquals(decoded.getUpperLimit(), new BigDecimal(9001));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("origin");
    jsonProperties.add("period");
    jsonProperties.add("factor");
    jsonProperties.add("lowerLimit");
    jsonProperties.add("upperLimit");
    jsonProperties.add("dimensions");
    jsonProperties.add("data");
    geneticJsonContainsFieldsTest(TestModels.sampledData, jsonProperties);
  }
}