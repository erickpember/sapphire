// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for the When element in the Order model.
 */
public class OrderWhenTest extends ModelTestBase {
  @Test
  public <T extends Object> void testOrderWhen() throws IOException, URISyntaxException {
    OrderWhen decoded = (OrderWhen) geneticEncodeDecodeTest(TestModels.orderWhen);

    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getSchedule(), TestModels.timing);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("schedule");

    geneticJsonContainsFieldsTest(TestModels.orderWhen, jsonProperties);
  }
}
