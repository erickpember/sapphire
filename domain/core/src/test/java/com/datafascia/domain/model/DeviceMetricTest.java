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
 * Test code for the DeviceMetric Model, part of the ObservationDevice Element, from Observation.
 */
public class DeviceMetricTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDeviceMetric() throws IOException, URISyntaxException {
    DeviceMetric decoded = (DeviceMetric) geneticEncodeDecodeTest(TestModels.deviceMetric);

    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getUnit(), TestModels.codeable);
    assertEquals(decoded.getCategory(), DeviceMetricCategory.CALCULATION);
    assertEquals(decoded.getColor(), DeviceMetricColor.BLACK);
    assertEquals(decoded.getOperationalStatus(), DeviceMetricOperationalStatus.OFF);
    assertEquals(decoded.getMeasurementPeriod(), TestModels.duration);
    assertEquals(decoded.getParentId(), Id.of("DeviceComponent"));
    assertEquals(decoded.getId(), Id.of("DeviceMetric"));
    assertEquals(decoded.getSourceId(), Id.of("Device"));
    assertEquals(decoded.getCalibrations(), Arrays.asList(TestModels.deviceMetricCalibration));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("calibrations");
    jsonProperties.add("category");
    jsonProperties.add("color");
    jsonProperties.add("@id");
    jsonProperties.add("measurementPeriod");
    jsonProperties.add("operationalStatus");
    jsonProperties.add("parentId");
    jsonProperties.add("sourceId");
    jsonProperties.add("type");
    jsonProperties.add("unit");

    geneticJsonContainsFieldsTest(TestModels.deviceMetric, jsonProperties);
  }
}
