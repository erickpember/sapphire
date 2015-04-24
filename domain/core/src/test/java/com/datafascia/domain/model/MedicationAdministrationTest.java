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
import static org.testng.Assert.assertTrue;

/**
 * Test code for medicationAdministration model.
 */
public class MedicationAdministrationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationAdministration()
      throws IOException, URISyntaxException {
    MedicationAdministration decoded
        = (MedicationAdministration) geneticEncodeDecodeTest(
            TestModels.medicationAdministration);

    assertTrue(decoded.getWasNotGiven());
    assertEquals(decoded.getDeviceId(), Id.of("device"));
    assertEquals(decoded.getEncounterId(), Id.of("encounter"));
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getMedicationId(), Id.of("medication"));
    assertEquals(decoded.getPrescriptionId(), Id.of("prescription"));
    assertEquals(decoded.getPatientId(), Id.of("patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("practitioner"));
    assertEquals(decoded.getEffectiveTime(), TestModels.period);
    assertEquals(decoded.getReasonsGiven(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getReasonsNotGiven(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getDosage(), TestModels.medicationAdministrationDosage);
    assertEquals(decoded.getStatus(), MedicationAdministrationStatus.IN_PROGRESS);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("dosage");
    jsonProperties.add("effectiveTime");
    jsonProperties.add("encounterId");
    jsonProperties.add("@id");
    jsonProperties.add("medicationId");
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");
    jsonProperties.add("prescription");
    jsonProperties.add("prescriptionId");
    jsonProperties.add("reasonsNotGiven");
    jsonProperties.add("reasonsNotGiven");
    jsonProperties.add("status");

    geneticJsonContainsFieldsTest(TestModels.medicationAdministration, jsonProperties);
  }
}
