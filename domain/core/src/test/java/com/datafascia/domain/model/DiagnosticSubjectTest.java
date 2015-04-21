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
 * Test code for the Subject Element of the DiagnosticReport model.
 */
public class DiagnosticSubjectTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDiagnosticSubject() throws IOException, URISyntaxException {
    DiagnosticSubject decoded = (DiagnosticSubject) geneticEncodeDecodeTest(
        TestModels.diagnosticSubject);

    assertEquals(decoded.getDeviceId(), Id.of("Device"));
    assertEquals(decoded.getGroupId(), Id.of("Group"));
    assertEquals(decoded.getLocationId(), Id.of("Location"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("groupId");
    jsonProperties.add("locationId");
    jsonProperties.add("patientId");

    geneticJsonContainsFieldsTest(TestModels.diagnosticSubject, jsonProperties);
  }
}