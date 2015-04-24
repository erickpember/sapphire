// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for medicationAdministrationDosage model.
 */
public class MedicationAdministrationDosageTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationAdministrationDosage()
      throws IOException, URISyntaxException {
    MedicationAdministrationDosage decoded
        = (MedicationAdministrationDosage) geneticEncodeDecodeTest(
            TestModels.medicationAdministrationDosage);

    assertEquals(decoded.getMethod(), TestModels.codeable);
    assertEquals(decoded.getRoute(), TestModels.codeable);
    assertEquals(decoded.getSite(), TestModels.codeable);
    assertEquals(decoded.getQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getRate(), TestModels.ratio);
    assertEquals(decoded.getText(), "text");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("method");
    jsonProperties.add("quantity");
    jsonProperties.add("rate");
    jsonProperties.add("route");
    jsonProperties.add("site");
    jsonProperties.add("text");

    geneticJsonContainsFieldsTest(TestModels.medicationAdministrationDosage, jsonProperties);
  }
}
