// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link MedicationAdministration} test
 */
public class MedicationAdministrationTest extends ModelTestBase {

  @Test
  public void testMedicationAdministration() throws Exception {
    MedicationAdministration decoded = (MedicationAdministration) geneticEncodeDecodeTest(
        TestModels.medicationAdministration);

    assertEquals(decoded.getWasNotGiven(), Boolean.TRUE);
    assertEquals(decoded.getDeviceId(), Id.of("device"));
    assertEquals(decoded.getEncounterId(), Id.of("encounter"));
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getMedicationId(), Id.of("medication"));
    assertEquals(decoded.getPrescriptionId(), Id.of("prescription"));
    assertEquals(decoded.getPatientId(), Id.of("patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("practitioner"));
    assertEquals(decoded.getEffectiveTimePeriod(), TestModels.period);
    assertEquals(decoded.getReasonsGiven(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getReasonsNotGiven(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getDosage(), TestModels.medicationAdministrationDosage);
    assertEquals(decoded.getStatus(), MedicationAdministrationStatus.IN_PROGRESS);
  }

  @Test
  public void testJsonProperties() throws Exception {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("dosage");
    jsonProperties.add("effectiveTimePeriod");
    jsonProperties.add("encounterId");
    jsonProperties.add("@id");
    jsonProperties.add("medicationId");
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");
    jsonProperties.add("prescriptionId");
    jsonProperties.add("reasonsNotGiven");
    jsonProperties.add("reasonsGiven");
    jsonProperties.add("status");

    geneticJsonContainsFieldsTest(TestModels.medicationAdministration, jsonProperties);
  }
}
