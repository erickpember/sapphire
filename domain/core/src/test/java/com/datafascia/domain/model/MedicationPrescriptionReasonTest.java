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
 * Test code for MedicationPrescriptionReason model.
 */
public class MedicationPrescriptionReasonTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationPrescriptionReason()
      throws IOException, URISyntaxException {
    MedicationPrescriptionReason decoded
        = (MedicationPrescriptionReason) geneticEncodeDecodeTest(
            TestModels.medicationPrescriptionReason);

    assertEquals(decoded.getCode(),TestModels.codeable);
    assertEquals(decoded.getConditionId(), Id.of("condition"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("conditionId");

    geneticJsonContainsFieldsTest(TestModels.medicationPrescriptionReason, jsonProperties);
  }
}
