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
 * Test code for the DiagnosticOrderSupportingInformation Element in the DiagnosticOrder model.
 */
public class DiagnosticOrderSupportingInformationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDiagnosticOrderSupportingInformation()
      throws IOException, URISyntaxException {
    DiagnosticOrderSupportingInformation decoded
        = (DiagnosticOrderSupportingInformation) geneticEncodeDecodeTest(
            TestModels.diagnosticOrderSupportingInformation);

    assertEquals(decoded.getConditionId(), Id.of("Condition"));
    assertEquals(decoded.getDocumentReferenceId(), Id.of("DocumentReference"));
    assertEquals(decoded.getObservationId(), Id.of("Observation"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("conditionId");
    jsonProperties.add("documentReferenceId");
    jsonProperties.add("observationId");

    geneticJsonContainsFieldsTest(TestModels.diagnosticOrderSupportingInformation, jsonProperties);
  }
}
