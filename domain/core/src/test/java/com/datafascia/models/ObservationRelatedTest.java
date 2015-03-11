// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ObservationRelated model.
 */
public class ObservationRelatedTest extends ModelTestBase {
  @Test
  public <T extends Object> void testObservationRelated() throws IOException, URISyntaxException {
    ObservationRelated decoded = (ObservationRelated) geneticEncodeDecodeTest(TestModels.related);
    assertEquals(decoded.getType(), Arrays.asList(ObservationRelationshipType.DerivedFrom));
    assertEquals(decoded.getTarget(), TestModels.getURI());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("type");
    jsonProperties.add("target");
    geneticJsonContainsFieldsTest(TestModels.related, jsonProperties);
  }
}