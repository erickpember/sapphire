// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for EncounterAccomodation model.
 */
public class EncounterAccomodationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testEncounterAccomodationn() throws IOException,
      URISyntaxException {
    EncounterAccomodation decoded
        = (EncounterAccomodation) geneticEncodeDecodeTest(TestModels.accomodation);
    assertEquals(decoded.getBed(), TestModels.getURI());
    assertEquals(decoded.getPeriod(), TestModels.period);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("bed");
    jsonProperties.add("period");
    geneticJsonContainsFieldsTest(TestModels.accomodation, jsonProperties);
  }
}
