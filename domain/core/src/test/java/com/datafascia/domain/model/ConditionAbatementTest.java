// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Test code for ConditionAbatement model.
 */
public class ConditionAbatementTest extends ModelTestBase {
  @Test
  public <T extends Object> void testConditionAbatement() throws IOException, URISyntaxException {
    ConditionAbatement decoded
        = (ConditionAbatement) geneticEncodeDecodeTest(TestModels.conditionAbatement);

    assertFalse(decoded.getAbatementBoolean());
    assertEquals(decoded.getAbatementPeriod(), TestModels.period);
    assertEquals(decoded.getAbatementDate(), TestModels.getDate());
    assertEquals(decoded.getAbatementAge(), TestModels.numericQuantity);
    assertEquals(decoded.getAbatementRange(), TestModels.range);
    assertEquals(decoded.getAbatementString(), "abatement");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("abatementAge");
    jsonProperties.add("abatementBoolean");
    jsonProperties.add("abatementDate");
    jsonProperties.add("abatementRange");
    jsonProperties.add("abatementString");
    jsonProperties.add("abatementPeriod");
    geneticJsonContainsFieldsTest(TestModels.conditionAbatement, jsonProperties);
  }
}
