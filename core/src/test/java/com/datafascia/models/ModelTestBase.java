// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.urn.URNMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.BeforeSuite;

import static org.testng.Assert.assertEquals;

/**
 * Base class for tests of models.
 */
public class ModelTestBase {
  public static ObjectMapper mapper = new ObjectMapper();

  @BeforeSuite
  public void setup() {
    // Load the mappings by scanning the package
    URNMap.idNSMapping("com.datafascia.models");
  }

  public Object geneticEncodeDecodeTest(Object test) throws IOException, URISyntaxException {
    String jsonString = mapper.writeValueAsString(test);
    Object decoded= mapper.readValue(jsonString, test.getClass());
    assertEquals(decoded, test);
    return decoded;
  }
}
