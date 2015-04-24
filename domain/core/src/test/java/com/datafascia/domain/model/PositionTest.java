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
 * Test code for Position model.
 */
public class PositionTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPosition() throws IOException, URISyntaxException {
    Position decoded = (Position) geneticEncodeDecodeTest(TestModels.position);

    assertEquals(decoded.getAltitude(), new BigDecimal(109));
    assertEquals(decoded.getLatitude(), new BigDecimal(56.185158));
    assertEquals(decoded.getLongitude(), new BigDecimal(-4.050253));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("altitude");
    jsonProperties.add("latitude");
    jsonProperties.add("longitude");

    geneticJsonContainsFieldsTest(TestModels.position, jsonProperties);
  }
}
