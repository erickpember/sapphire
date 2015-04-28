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
 * Test code for the Contacts Element of the Patient Model.
 */
public class PatientContactTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPatientContact() throws IOException, URISyntaxException {
    PatientContact decoded = (PatientContact) geneticEncodeDecodeTest(TestModels.patientContact);

    assertEquals(decoded.getAddress(), TestModels.address);
    assertEquals(decoded.getGender(), Gender.FEMALE);
    assertEquals(decoded.getName(), TestModels.humanName);
    assertEquals(decoded.getOrganizationId(), Id.of("Organization"));
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getRelationships(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getTelecoms(), Arrays.asList(TestModels.contactPoint));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("address");
    jsonProperties.add("gender");
    jsonProperties.add("name");
    jsonProperties.add("organizationId");
    jsonProperties.add("period");
    jsonProperties.add("relationships");
    jsonProperties.add("telecoms");

    geneticJsonContainsFieldsTest(TestModels.patientContact, jsonProperties);
  }
}
