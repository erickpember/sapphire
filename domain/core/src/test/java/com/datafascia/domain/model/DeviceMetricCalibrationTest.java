// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for DeviceMetricCalibration model.
 */
public class DeviceMetricCalibrationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDeviceMetricCalibration() throws IOException,
      URISyntaxException {
    DeviceMetricCalibration decoded = (DeviceMetricCalibration) geneticEncodeDecodeTest(
        TestModels.deviceMetricCalibration);

    assertEquals(decoded.getState(), DeviceMetricCalibrationState.CALIBRATED);
    assertEquals(decoded.getType(), DeviceMetricCalibrationType.GAIN);
    assertEquals(decoded.getTime(), TestModels.getDateTime());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("state");
    jsonProperties.add("time");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.deviceMetricCalibration, jsonProperties);
  }
}
