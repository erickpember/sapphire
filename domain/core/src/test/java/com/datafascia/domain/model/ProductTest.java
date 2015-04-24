// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Product model.
 */
public class ProductTest extends ModelTestBase {
  @Test
  public <T extends Object> void testProduct() throws IOException, URISyntaxException {
    Product decoded = (Product) geneticEncodeDecodeTest(TestModels.product);

    assertEquals(decoded.getForm(), TestModels.codeable);
    assertEquals(decoded.getIngredients(), Arrays.asList(TestModels.ingredient));
    assertEquals(decoded.getBatches(), Arrays.asList(TestModels.productBatch));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("form");
    jsonProperties.add("ingredients");
    jsonProperties.add("batches");

    geneticJsonContainsFieldsTest(TestModels.product, jsonProperties);
  }
}
