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
 * Test code for Device model.
 */
public class DeviceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDevice() throws IOException, URISyntaxException {
    Device decoded = (Device) geneticEncodeDecodeTest(TestModels.device);

    assertEquals(decoded.getContact(), TestModels.contactPoint);
    assertEquals(decoded.getStatus(), ContactPartyType.PATINF);
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getPatientId(), Id.of("patient"));
    assertEquals(decoded.getExpiry(), TestModels.getDateTime());
    assertEquals(decoded.getManufactureDate(), TestModels.getDateTime());
    assertEquals(decoded.getLocation(), TestModels.location);
    assertEquals(decoded.getOwnerId(), Id.of("organization"));
    assertEquals(decoded.getLotNumber(), "lot number");
    assertEquals(decoded.getManufacturer(), "manufacturer");
    assertEquals(decoded.getModel(), "model");
    assertEquals(decoded.getUdi(), "udi");
    assertEquals(decoded.getVersion(), "version");
    assertEquals(decoded.getUrl(), TestModels.getURI());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("contact");
    jsonProperties.add("expiry");
    jsonProperties.add("@id");
    jsonProperties.add("location");
    jsonProperties.add("lotNumber");
    jsonProperties.add("manufactureDate");
    jsonProperties.add("manufacturer");
    jsonProperties.add("model");
    jsonProperties.add("ownerId");
    jsonProperties.add("patientId");
    jsonProperties.add("status");
    jsonProperties.add("type");
    jsonProperties.add("udi");
    jsonProperties.add("url");
    jsonProperties.add("version");

    geneticJsonContainsFieldsTest(TestModels.device, jsonProperties);
  }
}
