// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for CodeableConcept model.
 */
public class CodeableConceptTest extends ModelTestBase {
  @Test
  public <T extends Object> void testCodableConcept() throws IOException, URISyntaxException {
    CodeableConcept decoded = (CodeableConcept) geneticEncodeDecodeTest(TestModels.codeable);
    assertEquals(decoded.getCode(), "Codeable");
    assertEquals(decoded.getText(), "Concept");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("text");
    geneticJsonContainsFieldsTest(TestModels.codeable, jsonProperties);
  }
}
