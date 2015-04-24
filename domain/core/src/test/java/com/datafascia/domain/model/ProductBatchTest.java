// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ProductBatch model.
 */
public class ProductBatchTest extends ModelTestBase {
  @Test
  public <T extends Object> void testProductBatch() throws IOException, URISyntaxException {
    ProductBatch decoded = (ProductBatch) geneticEncodeDecodeTest(TestModels.productBatch);

    assertEquals(decoded.getExpirationDate(),TestModels.getDateTime());
    assertEquals(decoded.getLotNumber(), "lotNumber");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("expirationDate");
    jsonProperties.add("lotNumber");

    geneticJsonContainsFieldsTest(TestModels.productBatch, jsonProperties);
  }
}
