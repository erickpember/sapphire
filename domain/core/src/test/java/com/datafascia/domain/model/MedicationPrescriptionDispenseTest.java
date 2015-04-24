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
 * Test code for MedicationPrescriptionDispense model.
 */
public class MedicationPrescriptionDispenseTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationPrescriptionDispense()
      throws IOException, URISyntaxException {
    MedicationPrescriptionDispense decoded
        = (MedicationPrescriptionDispense) geneticEncodeDecodeTest(
            TestModels.medicationPrescriptionDispense);

    assertEquals(decoded.getMedicationId(), Id.of("medication"));
    assertEquals(decoded.getNumberOfRepeatsAllowed(), (Integer) 9001);
    assertEquals(decoded.getValidityPeriod(), TestModels.period);
    assertEquals(decoded.getDispenseQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getExpectedSupplyDuration(), TestModels.numericQuantity);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("dispenseQuantity");
    jsonProperties.add("expectedSupplyDuration");
    jsonProperties.add("medicationId");
    jsonProperties.add("numberOfRepeatsAllowed");
    jsonProperties.add("validityPeriod");

    geneticJsonContainsFieldsTest(TestModels.medicationPrescriptionDispense, jsonProperties);
  }
}
