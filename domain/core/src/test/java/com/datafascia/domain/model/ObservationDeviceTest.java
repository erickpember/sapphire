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
 * Test code for the Device element in the Observation model.
 */
public class ObservationDeviceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testObservationDevice() throws IOException, URISyntaxException {
    ObservationDevice decoded = (ObservationDevice) geneticEncodeDecodeTest(
        TestModels.observationDevice);

    assertEquals(decoded.getDeviceId(), Id.of("Device"));
    assertEquals(decoded.getDeviceMetricId(), Id.of("DeviceMetric"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("deviceMetricId");

    geneticJsonContainsFieldsTest(TestModels.observationDevice, jsonProperties);
  }
}
