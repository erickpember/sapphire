// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.jackson.DFObjectMapper;
import com.datafascia.urn.URNMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.BeforeSuite;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Base class for tests of models.
 */
public class ModelTestBase {
  public static ObjectMapper mapper = DFObjectMapper.objectMapper();

  @BeforeSuite
  public void setup() {
    // Load the mappings by scanning the package
    URNMap.idNSMapping("com.datafascia.models");
  }

  public Object geneticEncodeDecodeTest(Object test) throws IOException, URISyntaxException {
    String jsonString = mapper.writeValueAsString(test);
    Object decoded = mapper.readValue(jsonString, test.getClass());
    assertEquals(decoded, test, "Failed to find in Json String: " + jsonString);
    return decoded;
  }

  /**
   * Serialize the object to Json and make sure the values have the key names specified in the
   * JsonProperty annotations.
   *
   * @param test tested object
   * @param jsonProperties Json property names we are looking for
   * @throws JsonProcessingException
   * @throws IOException
   */
  public void geneticJsonContainsFieldsTest(Object test, ArrayList<String> jsonProperties)
      throws JsonProcessingException, IOException {
    String jsonString = mapper.writeValueAsString(test);

    // Deserialize to JsonNode instead of back to the tested class, so Json field names are seen
    JsonNode jsonObject = mapper.readTree(jsonString);

    for (String property : jsonProperties) {
      assertNotNull(jsonObject.findValue(property), "Failed to find Json Property:" + property
          + " in json: " + jsonString);
    }
  }
}
