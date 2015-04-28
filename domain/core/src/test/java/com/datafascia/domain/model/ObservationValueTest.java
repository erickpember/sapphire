// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for the ObservationValue Element of the Observation model.
 */
public class ObservationValueTest extends ModelTestBase {
  @Test
  public <T extends Object> void testObservationValue() throws IOException, URISyntaxException {
    ObservationValue decoded
        = (ObservationValue) geneticEncodeDecodeTest(TestModels.observationValue);
    assertEquals(decoded.getAttachment(), TestModels.attachment);
    assertEquals(decoded.getCodeableConcept(), TestModels.codeable);
    assertEquals(decoded.getTime(), TestModels.duration);
    assertEquals(decoded.getDateTime(), TestModels.getDateTime());
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getRange(), TestModels.range);
    assertEquals(decoded.getRatio(), TestModels.ratio);
    assertEquals(decoded.getSampledData(), TestModels.sampledData);
    assertEquals(decoded.getString(), "string");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("attachment");
    jsonProperties.add("codeableConcept");
    jsonProperties.add("dateTime");
    jsonProperties.add("period");
    jsonProperties.add("quantity");
    jsonProperties.add("range");
    jsonProperties.add("ratio");
    jsonProperties.add("sampleData");
    jsonProperties.add("string");
    jsonProperties.add("time");

    geneticJsonContainsFieldsTest(TestModels.observationValue, jsonProperties);
  }
}
