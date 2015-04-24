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
 * Test code for DiagnosticOrderEventActor model.
 */
public class DiagnosticOrderEventActorTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDiagnosticOrderEventActor()
      throws IOException, URISyntaxException {
    DiagnosticOrderEventActor decoded
        = (DiagnosticOrderEventActor) geneticEncodeDecodeTest(TestModels.diagnosticOrderEventActor);

    assertEquals(decoded.getDeviceId(), Id.of("Device"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("practitionerId");

    geneticJsonContainsFieldsTest(TestModels.diagnosticOrderEventActor, jsonProperties);
  }
}
