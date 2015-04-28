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
 * Test code for the Dosage Element in the MedicationStatement model.
 */
public class MedicationStatementDosageTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationStatementDosage() throws IOException,
      URISyntaxException {
    MedicationStatementDosage decoded = (MedicationStatementDosage) geneticEncodeDecodeTest(
        TestModels.medicationStatementDosage);

    assertTrue(decoded.getAsNeededBoolean());
    assertEquals(decoded.getAsNeededCodeableConcept(), TestModels.codeable);
    assertEquals(decoded.getMethod(), TestModels.codeable);
    assertEquals(decoded.getRoute(), TestModels.codeable);
    assertEquals(decoded.getSite(), TestModels.codeable);
    assertEquals(decoded.getQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getMaxDosePerPeriod(), TestModels.ratio);
    assertEquals(decoded.getRate(), TestModels.ratio);
    assertEquals(decoded.getSchedule(), TestModels.timing);
    assertEquals(decoded.getText(), "text");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("asNeededBoolean");
    jsonProperties.add("asNeededCodeableConcept");
    jsonProperties.add("maxDosePerPeriod");
    jsonProperties.add("method");
    jsonProperties.add("quantity");
    jsonProperties.add("rate");
    jsonProperties.add("route");
    jsonProperties.add("schedule");
    jsonProperties.add("site");
    jsonProperties.add("text");

    geneticJsonContainsFieldsTest(TestModels.medicationStatementDosage, jsonProperties);
  }
}
