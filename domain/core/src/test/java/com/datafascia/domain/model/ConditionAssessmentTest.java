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
 * Test code for ConditionAssessment model.
 */
public class ConditionAssessmentTest extends ModelTestBase {
  @Test
  public <T extends Object> void testConditionAssessment() throws IOException,
      URISyntaxException {
    ConditionAssessment decoded = (ConditionAssessment) geneticEncodeDecodeTest(
        TestModels.conditionAssessment);

    assertEquals(decoded.getConditionId(), Id.of("Condition"));
    assertEquals(decoded.getImmunizationId(), Id.of("Immunization"));
    assertEquals(decoded.getMedicationAdministrationId(), Id.of("MedicationAdministration"));
    assertEquals(decoded.getMedicationStatementId(), Id.of("MedicationStatement"));
    assertEquals(decoded.getProcedureId(), Id.of("Procedure"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("conditionId");
    jsonProperties.add("immunizationId");
    jsonProperties.add("medicationAdministrationId");
    jsonProperties.add("medicationStatementId");
    jsonProperties.add("procedureId");

    geneticJsonContainsFieldsTest(TestModels.conditionAssessment, jsonProperties);
  }
}
