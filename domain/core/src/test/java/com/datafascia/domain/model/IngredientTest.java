// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Ingredient model.
 */
public class IngredientTest extends ModelTestBase {
  @Test
  public <T extends Object> void testIngredient() throws IOException, URISyntaxException {
    Ingredient decoded = (Ingredient) geneticEncodeDecodeTest(TestModels.ingredient);

    assertEquals(decoded.getItem(), TestModels.ingredientItem);
    assertEquals(decoded.getAmount(), TestModels.ratio);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("item");
    jsonProperties.add("amount");

    geneticJsonContainsFieldsTest(TestModels.ingredient, jsonProperties);
  }
}
