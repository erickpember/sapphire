// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ObservationValue model.
 */
public class ObservationValueTest extends ModelTestBase {
  @Test
  public <T extends Object> void testObservationValue() throws IOException, URISyntaxException {
    ObservationValue decoded
        = (ObservationValue) geneticEncodeDecodeTest(TestModels.observationValue);
    assertEquals(decoded.getQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getAttachment(), TestModels.attachment);
    assertEquals(decoded.getRatio(), TestModels.ratio);
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getSampledData(), TestModels.sampledData);
    assertEquals(decoded.getText(), "An observation");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("quantity");
    jsonProperties.add("code");
    jsonProperties.add("attachment");
    jsonProperties.add("ratio");
    jsonProperties.add("period");
    jsonProperties.add("sampleData");
    jsonProperties.add("text");
    geneticJsonContainsFieldsTest(TestModels.observationValue, jsonProperties);
  }
}
