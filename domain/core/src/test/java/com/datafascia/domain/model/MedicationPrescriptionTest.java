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
 * Test code for MedicationPrescription model.
 */
public class MedicationPrescriptionTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationPrescription()
      throws IOException, URISyntaxException {
    MedicationPrescription decoded
        = (MedicationPrescription) geneticEncodeDecodeTest(TestModels.medicationPrescription);

    assertEquals(decoded.getEncounterId(), Id.of("encounter"));
    assertEquals(decoded.getMedicationId(), Id.of("medication"));
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getPatientId(), Id.of("patient"));
    assertEquals(decoded.getPrescriberId(), Id.of("prescriber"));
    assertEquals(decoded.getDateWritten(), TestModels.getDateTime());
    assertEquals(decoded.getDosageInstructions(),
        Arrays.asList(TestModels.medicationPrescriptionDosageInstruction));
    assertEquals(decoded.getDispense(), TestModels.medicationPrescriptionDispense);
    assertEquals(decoded.getReason(), TestModels.medicationPrescriptionReason);
    assertEquals(decoded.getStatus(), MedicationPrescriptionStatus.ACTIVE);
    assertEquals(decoded.getSubstitution(), TestModels.medicationPrescriptionSubstitution);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("dateWritten");
    jsonProperties.add("dispense");
    jsonProperties.add("dosageInstructions");
    jsonProperties.add("encounterId");
    jsonProperties.add("@id");
    jsonProperties.add("medicationId");
    jsonProperties.add("patientId");
    jsonProperties.add("prescriberId");
    jsonProperties.add("reason");
    jsonProperties.add("status");
    jsonProperties.add("substitution");

    geneticJsonContainsFieldsTest(TestModels.medicationPrescription, jsonProperties);
  }
}
