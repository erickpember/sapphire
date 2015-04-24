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
 * Test code for Content model.
 */
public class ContentTest extends ModelTestBase {
  @Test
  public <T extends Object> void testContent() throws IOException, URISyntaxException {
    Content decoded = (Content) geneticEncodeDecodeTest(TestModels.content);

    assertEquals(decoded.getItemId(), Id.of("item"));
    assertEquals(decoded.getAmount(), TestModels.numericQuantity);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("itemId");
    jsonProperties.add("amount");

    geneticJsonContainsFieldsTest(TestModels.content, jsonProperties);
  }
}
