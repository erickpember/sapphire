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
 * Test code for the DeviceComponent element of the DeviceMetric model.
 */
public class DeviceComponentTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDeviceComponent() throws IOException, URISyntaxException {
    DeviceComponent decoded = (DeviceComponent) geneticEncodeDecodeTest(TestModels.deviceComponent);

    assertEquals(decoded.getLanguageCode(), TestModels.codeable);
    assertEquals(decoded.getParameterGroup(), TestModels.codeable);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getMeasurementPrinciple(), DeviceComponentMeasurementPrinciple.ACOUSTICAL);
    assertEquals(decoded.getId(), Id.of("DeviceComponent"));
    assertEquals(decoded.getParentId(), Id.of("DeviceComponent"));
    assertEquals(decoded.getSourceId(), Id.of("Device"));
    assertEquals(decoded.getLastSystemChange(), TestModels.getDateTime());
    assertEquals(decoded.getOperationalStatuses(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getProductionSpecifications(), Arrays.asList(
        TestModels.deviceComponentSpecification));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("@id");
    jsonProperties.add("languageCode");
    jsonProperties.add("lastSystemChange");
    jsonProperties.add("measurementPrinciple");
    jsonProperties.add("operationalStatuses");
    jsonProperties.add("parameterGroup");
    jsonProperties.add("parentId");
    jsonProperties.add("productionSpecifications");
    jsonProperties.add("sourceId");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.deviceComponent, jsonProperties);
  }
}
