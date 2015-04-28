// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for the Animal Element of the Patient Model.
 */
public class PatientAnimalTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPatientAnimal() throws IOException, URISyntaxException {
    PatientAnimal decoded = (PatientAnimal) geneticEncodeDecodeTest(TestModels.patientAnimal);

    assertEquals(decoded.getBreed(), TestModels.codeable);
    assertEquals(decoded.getGenderStatus(), TestModels.codeable);
    assertEquals(decoded.getSpecies(), TestModels.codeable);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("breed");
    jsonProperties.add("genderStatus");
    jsonProperties.add("species");

    geneticJsonContainsFieldsTest(TestModels.patientAnimal, jsonProperties);
  }
}
