// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Substance model.
 */
public class SubstanceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSubstance() throws IOException, URISyntaxException {
    Substance decoded = (Substance) geneticEncodeDecodeTest(TestModels.substance);

    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getExpiry(), TestModels.getDateTime());
    assertEquals(decoded.getIngredients(), Arrays.asList(TestModels.substanceIngredient));
    assertEquals(decoded.getQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getDescription(), "description");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("description");
    jsonProperties.add("expiry");
    jsonProperties.add("@id");
    jsonProperties.add("ingredients");
    jsonProperties.add("quantity");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.substance, jsonProperties);
  }
}
