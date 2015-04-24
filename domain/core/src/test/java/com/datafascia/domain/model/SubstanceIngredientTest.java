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
 * Test code for SubstanceIngredient model.
 */
public class SubstanceIngredientTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSubstanceIngredient() throws IOException, URISyntaxException {
    SubstanceIngredient decoded
        = (SubstanceIngredient) geneticEncodeDecodeTest(TestModels.substanceIngredient);

    assertEquals(decoded.getSubstanceId(), Id.of("substance"));
    assertEquals(decoded.getQuantity(), TestModels.ratio);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("substanceId");
    jsonProperties.add("quantity");

    geneticJsonContainsFieldsTest(TestModels.substanceIngredient, jsonProperties);
  }
}
