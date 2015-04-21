// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ConditionEvidence model.
 */
public class ConditionEvidenceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testConditionEvidence() throws IOException, URISyntaxException {
    ConditionEvidence decoded
        = (ConditionEvidence) geneticEncodeDecodeTest(TestModels.conditionEvidence);

    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getDetails(), Arrays.asList(TestModels.getURI()));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("details");

    geneticJsonContainsFieldsTest(TestModels.conditionEvidence, jsonProperties);
  }
}