// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for caregiver model.
 */
public class CaregiverTest extends ModelTestBase {
  @Test
  public <T extends Object> void testCaregiver() throws IOException, URISyntaxException {
    Caregiver decoded = (Caregiver) geneticEncodeDecodeTest(TestModels.caregiver);
    assertEquals(decoded.getAddress(), TestModels.address);
    assertEquals(decoded.getSpecialty(), Specialty.Allergy);
    assertEquals(decoded.getName(), TestModels.name);
    assertEquals(decoded.getGender(), Gender.Undifferentiated);
    assertEquals(decoded.getBirthDate(), TestModels.getDate());
    assertEquals(decoded.getPhoto(), TestModels.getURI());
    assertEquals(decoded.getOrganization(), "Test Corp.");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("specialty");
    jsonProperties.add("name");
    jsonProperties.add("address");
    jsonProperties.add("gender");
    jsonProperties.add("birthDate");
    jsonProperties.add("photo");
    jsonProperties.add("organization");
    geneticJsonContainsFieldsTest(TestModels.caregiver, jsonProperties);
  }
}
