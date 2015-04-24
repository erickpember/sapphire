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
 * Test code for the MedicationStatement model.
 */
public class MedicationStatementTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationStatement() throws IOException, URISyntaxException {
    MedicationStatement decoded = (MedicationStatement) geneticEncodeDecodeTest(
        TestModels.medicationStatement);

    assertTrue(decoded.getWasNotGiven());
    assertEquals(decoded.getReasonForUseCodeableConcept(), TestModels.codeable);
    assertEquals(decoded.getReasonForUseReferenceId(), Id.of("Condition"));
    assertEquals(decoded.getMedicationId(), Id.of("Medication"));
    assertEquals(decoded.getId(), Id.of("MedicationStatement"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getDateAsserted(), TestModels.getDateTime());
    assertEquals(decoded.getEffectiveDateTime(), TestModels.getDateTime());
    assertEquals(decoded.getEffectivePeriod(), TestModels.period);
    assertEquals(decoded.getReasonsNotGiven(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getDosages(), Arrays.asList(TestModels.medicationStatementDosage));
    assertEquals(decoded.getInformationSource(), TestModels.medicationStatementInformationSource);
    assertEquals(decoded.getStatus(), MedicationStatementStatus.COMPLETED);
    assertEquals(decoded.getNote(), "note");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("dateAsserted");
    jsonProperties.add("dosages");
    jsonProperties.add("effectiveDateTime");
    jsonProperties.add("effectivePeriod");
    jsonProperties.add("@id");
    jsonProperties.add("informationSource");
    jsonProperties.add("medicationId");
    jsonProperties.add("note");
    jsonProperties.add("patientId");
    jsonProperties.add("reasonForUseCodeableConcept");
    jsonProperties.add("reasonForUseReferenceId");
    jsonProperties.add("reasonsNotGiven");
    jsonProperties.add("status");
    jsonProperties.add("wasNotGiven");

    geneticJsonContainsFieldsTest(TestModels.medicationStatement, jsonProperties);
  }
}
