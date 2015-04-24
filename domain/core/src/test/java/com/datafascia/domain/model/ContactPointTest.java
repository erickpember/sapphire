// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ContactPoint model.
 */
public class ContactPointTest extends ModelTestBase {
  @Test
  public <T extends Object> void testContactPoint() throws IOException, URISyntaxException {
    ContactPoint decoded = (ContactPoint) geneticEncodeDecodeTest(TestModels.contactPoint);

    assertEquals(decoded.getSystem(), ContactPointSystem.EMAIL);
    assertEquals(decoded.getValue(), "zootsidenticaltwinsisterdingo@anthrax.castle");
    assertEquals(decoded.getUse(), ContactPointUse.WORK);
    assertEquals(decoded.getPeriod(), TestModels.period);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("system");
    jsonProperties.add("value");
    jsonProperties.add("use");
    jsonProperties.add("period");

    geneticJsonContainsFieldsTest(TestModels.contactPoint, jsonProperties);
  }
}
