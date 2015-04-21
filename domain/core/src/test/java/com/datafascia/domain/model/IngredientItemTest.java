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
 * Test code for IngredientItem model.
 */
public class IngredientItemTest extends ModelTestBase {
  @Test
  public <T extends Object> void testIngredientItem() throws IOException, URISyntaxException {
    IngredientItem decoded = (IngredientItem) geneticEncodeDecodeTest(TestModels.ingredientItem);

    assertEquals(decoded.getMedicationId(), Id.of("medication"));
    assertEquals(decoded.getSubstanceId(), Id.of("substance"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("medicationId");
    jsonProperties.add("substanceId");

    geneticJsonContainsFieldsTest(TestModels.ingredientItem, jsonProperties);
  }
}