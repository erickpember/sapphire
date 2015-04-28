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
 * Test code for the Location element of the Encounter model.
 */
public class EncounterLocationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testEncounterLocation() throws IOException, URISyntaxException {
    EncounterLocation decoded = (EncounterLocation) geneticEncodeDecodeTest(
        TestModels.encounterLocation);

    assertEquals(decoded.getStatus(), EncounterLocationStatus.PLANNED);
    assertEquals(decoded.getLocationId(), Id.of("Location"));
    assertEquals(decoded.getPeriod(), TestModels.period);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("locationId");
    jsonProperties.add("period");
    jsonProperties.add("status");

    geneticJsonContainsFieldsTest(TestModels.encounterLocation, jsonProperties);
  }
}
