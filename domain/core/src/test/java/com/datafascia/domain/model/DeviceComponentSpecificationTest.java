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
 * Test code for the DeviceComponentSpecification model.
 */
public class DeviceComponentSpecificationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDeviceComponentSpecification() throws IOException,
      URISyntaxException {
    DeviceComponentSpecification decoded = (DeviceComponentSpecification) geneticEncodeDecodeTest(
        TestModels.deviceComponentSpecification);

    assertEquals(decoded.getSpecType(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("DeviceComponentSpecification"));
    assertEquals(decoded.getProductionSpec(), "productionSpec");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("@id");
    jsonProperties.add("productionSpec");
    jsonProperties.add("specType");

    geneticJsonContainsFieldsTest(TestModels.deviceComponentSpecification, jsonProperties);
  }
}
