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
 * Test code for BooleanOrCodeableConcept model.
 */
public class BooleanOrCodeableConceptTest extends ModelTestBase {
  @Test
  public <T extends Object> void testBooleanOrCodeableConcept()
      throws IOException, URISyntaxException {
    BooleanOrCodeableConcept decoded
        = (BooleanOrCodeableConcept) geneticEncodeDecodeTest(TestModels.booleanOrCodeableConcept);

    assertFalse(decoded.getBool());
    assertEquals(decoded.getCode(), TestModels.codeable);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("bool");
    jsonProperties.add("code");
    geneticJsonContainsFieldsTest(TestModels.booleanOrCodeableConcept, jsonProperties);
  }
}
