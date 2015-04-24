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
 * Test code for ConditionDueToTarget model.
 */
public class ConditionDueToTargetTest extends ModelTestBase {
  @Test
  public <T extends Object> void testConditionDueToTarget() throws IOException, URISyntaxException {
    ConditionDueToTarget decoded = (ConditionDueToTarget) geneticEncodeDecodeTest(
        TestModels.conditionDueToTarget);
    assertEquals(decoded.getConditionId(), Id.of("Condition"));
    assertEquals(decoded.getImmunizationId(), Id.of("Immunization"));
    assertEquals(decoded.getMedicationAdministrationId(), Id.of("MedicationAdministration"));
    assertEquals(decoded.getMedicationStatementId(), Id.of("MedicationStatement"));
    assertEquals(decoded.getProcedureId(), Id.of("Procedure"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("ConditionId");
    jsonProperties.add("ImmunizationId");
    jsonProperties.add("MedicationAdministrationId");
    jsonProperties.add("MedicationStatementId");
    jsonProperties.add("ProcedureId");

    geneticJsonContainsFieldsTest(TestModels.conditionDueToTarget, jsonProperties);
  }
}
