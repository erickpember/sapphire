// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for medicationPrescriptionDosageInstruction model.
 */
public class MedicationPrescriptionDosageInstructionTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationPrescriptionDosageInstruction()
      throws IOException, URISyntaxException {
    MedicationPrescriptionDosageInstruction decoded
        = (MedicationPrescriptionDosageInstruction) geneticEncodeDecodeTest(
            TestModels.medicationPrescriptionDosageInstruction);

    assertEquals(decoded.getAsNeeded(), TestModels.booleanOrCodeableConcept);
    assertEquals(decoded.getAdditionalInstructions(), TestModels.codeable);
    assertEquals(decoded.getMethod(), TestModels.codeable);
    assertEquals(decoded.getRoute(), TestModels.codeable);
    assertEquals(decoded.getSite(), TestModels.codeable);
    assertEquals(decoded.getDoseQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getDoseRange(), TestModels.range);
    assertEquals(decoded.getMaxDosePerPeriod(), TestModels.ratio);
    assertEquals(decoded.getRate(), TestModels.ratio);
    assertEquals(decoded.getTiming(), TestModels.schedule);
    assertEquals(decoded.getText(), "text");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("additionalInstructions");
    jsonProperties.add("asNeeded");
    jsonProperties.add("doseQuantity");
    jsonProperties.add("doseRange");
    jsonProperties.add("maxDosePerPeriod");
    jsonProperties.add("method");
    jsonProperties.add("rate");
    jsonProperties.add("route");
    jsonProperties.add("site");
    jsonProperties.add("text");
    jsonProperties.add("timing");

    geneticJsonContainsFieldsTest(TestModels.medicationPrescriptionDosageInstruction,
        jsonProperties);
  }
}
