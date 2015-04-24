// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test code for ImmunizationReaction model.
 */
public class ImmunizationReactionTest extends ModelTestBase {
  @Test
  public <T extends Object> void testImmunizationReaction() throws IOException, URISyntaxException {
    ImmunizationReaction decoded = (ImmunizationReaction) geneticEncodeDecodeTest(
        TestModels.immunizationReaction);

    assertTrue(decoded.getReported());
    assertEquals(decoded.getDetailId(), Id.of("Observation"));
    assertEquals(decoded.getDate(), TestModels.getDateTime());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("date");
    jsonProperties.add("detailId");
    jsonProperties.add("reported");

    geneticJsonContainsFieldsTest(TestModels.immunizationReaction, jsonProperties);
  }
}
