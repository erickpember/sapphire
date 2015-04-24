// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Qualification model.
 */
public class QualificationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testQualification() throws IOException, URISyntaxException {
    Qualification decoded = (Qualification) geneticEncodeDecodeTest(TestModels.qualification);

    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getIssuerId(), Id.of("issuer"));
    assertEquals(decoded.getPeriod(), TestModels.period);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("issuerId");
    jsonProperties.add("period");

    geneticJsonContainsFieldsTest(TestModels.qualification, jsonProperties);
  }
}
