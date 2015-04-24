// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ConditionDueTo model.
 */
public class ConditionDueToTest extends ModelTestBase {
  @Test
  public <T extends Object> void testConditionDueTo() throws IOException, URISyntaxException {
    ConditionDueTo decoded = (ConditionDueTo) geneticEncodeDecodeTest(TestModels.conditionDueTo);

    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getTarget(), TestModels.conditionDueToTarget);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("target");

    geneticJsonContainsFieldsTest(TestModels.conditionDueTo, jsonProperties);
  }
}
