// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test code for Coding datatype.
 */
public class CodingTest extends ModelTestBase {
  @Test
  public <T extends Object> void testCoding() throws IOException, URISyntaxException {
    Coding decoded = (Coding) geneticEncodeDecodeTest(TestModels.coding);
    assertTrue(decoded.getPrimary());
    assertEquals(decoded.getCode(), "code");
    assertEquals(decoded.getDisplay(), "display");
    assertEquals(decoded.getVersion(), "version");
    assertEquals(decoded.getSystem(), TestModels.getURI());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("display");
    jsonProperties.add("primary");
    jsonProperties.add("system");
    jsonProperties.add("version");

    geneticJsonContainsFieldsTest(TestModels.coding, jsonProperties);
  }
}
