// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for MedicationPrescriptionSubstitution model.
 */
public class MedicationPrescriptionSubstitutionTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationPrescriptionSubstitution()
      throws IOException, URISyntaxException {
    MedicationPrescriptionSubstitution decoded
        = (MedicationPrescriptionSubstitution) geneticEncodeDecodeTest(
            TestModels.medicationPrescriptionSubstitution);

    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getReason(), TestModels.codeable);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("type");
    jsonProperties.add("reason");

    geneticJsonContainsFieldsTest(TestModels.medicationPrescriptionSubstitution, jsonProperties);
  }
}
