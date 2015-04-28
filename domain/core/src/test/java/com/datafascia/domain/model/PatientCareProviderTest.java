// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Patient Care Provider element in the Patient model.
 */
public class PatientCareProviderTest extends ModelTestBase {
  @Test
  public <T extends Object> void testCaregiver() throws IOException, URISyntaxException {
    PatientCareProvider decoded
        = (PatientCareProvider) geneticEncodeDecodeTest(TestModels.patientCareProvider);
    assertEquals(decoded.getPractitioner(), TestModels.practitioner);
    assertEquals(decoded.getOrganization(), TestModels.organization);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("practitioner");
    jsonProperties.add("organization");
    geneticJsonContainsFieldsTest(TestModels.patientCareProvider, jsonProperties);
  }
}
