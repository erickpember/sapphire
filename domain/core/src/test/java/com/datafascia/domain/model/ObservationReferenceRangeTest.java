// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ObservationReferenceRange model.
 */
public class ObservationReferenceRangeTest extends ModelTestBase {
  @Test
  public <T extends Object> void testReferenceRange() throws IOException, URISyntaxException {
    ObservationReferenceRange decoded = (ObservationReferenceRange) geneticEncodeDecodeTest(
        TestModels.observationReferenceRange);
    assertEquals(decoded.getMeaning(), TestModels.codeable);
    assertEquals(decoded.getAge(), TestModels.range);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("meaning");
    jsonProperties.add("age");
    geneticJsonContainsFieldsTest(TestModels.observationReferenceRange, jsonProperties);
  }
}
