// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.testng.Assert.assertEquals;

/**
 * Base class for tests of models.
 */
public class ModelTestBase {
  public static ObjectMapper mapper = new ObjectMapper();

  public void geneticEncodeDecodeTest(Object test) throws IOException, URISyntaxException {
    String jsonString = mapper.writeValueAsString(test);
    Object decoded= mapper.readValue(jsonString, test.getClass());
    assertEquals(test, decoded);
  }
}
