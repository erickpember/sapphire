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
 * Test code for the Related Elements in the Observation model.
 */
public class ObservationRelatedTest extends ModelTestBase {
  @Test
  public <T extends Object> void testObservationRelated() throws IOException, URISyntaxException {
    ObservationRelated decoded = (ObservationRelated) geneticEncodeDecodeTest(
        TestModels.observationRelated);
    assertEquals(decoded.getType(), Arrays.asList(ObservationRelationshipType.DERIVED_FROM));
    assertEquals(decoded.getTargetId(), Id.of("target"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("type");
    jsonProperties.add("targetId");
    geneticJsonContainsFieldsTest(TestModels.observationRelated, jsonProperties);
  }
}
