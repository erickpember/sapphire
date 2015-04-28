// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test code for the Communications Element of the Patient Model.
 */
public class PatientCommunicationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPatientCommunication() throws IOException, URISyntaxException {
    PatientCommunication decoded = (PatientCommunication) geneticEncodeDecodeTest(
        TestModels.patientCommunication);

    assertTrue(decoded.getPreferred());
    assertEquals(decoded.getLanguage(), TestModels.codeable);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("language");
    jsonProperties.add("preferred");

    geneticJsonContainsFieldsTest(TestModels.patientCommunication, jsonProperties);
  }
}
