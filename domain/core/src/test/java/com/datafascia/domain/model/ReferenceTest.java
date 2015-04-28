// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Reference model.
 */
public class ReferenceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testReference() throws IOException, URISyntaxException {
    Reference decoded = (Reference) geneticEncodeDecodeTest(TestModels.reference);

    assertEquals(decoded.getDisplay(), "display");
    assertEquals(decoded.getReference(), "reference");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("display");
    jsonProperties.add("reference");

    geneticJsonContainsFieldsTest(TestModels.reference, jsonProperties);
  }
}
