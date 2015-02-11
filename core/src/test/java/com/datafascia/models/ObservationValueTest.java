// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
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
    assertEquals(decoded.getQuantity(), TestModels.quantity);
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getAttachment(), TestModels.attachment);
    assertEquals(decoded.getRatio(), TestModels.ratio);
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getSampledData(), TestModels.sampledData);
    assertEquals(decoded.getText(), "An observation");
  }
}
