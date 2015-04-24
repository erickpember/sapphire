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
 * Test code for ConditionLocation model.
 */
public class ConditionLocationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testConditionLocation() throws IOException, URISyntaxException {
    ConditionLocation decoded
        = (ConditionLocation) geneticEncodeDecodeTest(TestModels.conditionLocation);

    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getBodySiteId(), Id.of("bodySite"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("bodySiteId");

    geneticJsonContainsFieldsTest(TestModels.conditionLocation, jsonProperties);
  }
}
