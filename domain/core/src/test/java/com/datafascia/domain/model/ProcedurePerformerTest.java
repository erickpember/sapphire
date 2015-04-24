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
 * Test code for the Performer Element in the Procedure model.
 */
public class ProcedurePerformerTest extends ModelTestBase {
  @Test
  public <T extends Object> void testProcedurePerformer() throws IOException, URISyntaxException {
    ProcedurePerformer decoded = (ProcedurePerformer) geneticEncodeDecodeTest(
        TestModels.procedurePerformer);

    assertEquals(decoded.getRole(), TestModels.codeable);
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
    assertEquals(decoded.getRelatedPersonId(), Id.of("RelatedPerson"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");
    jsonProperties.add("relatedPersonId");
    jsonProperties.add("role");

    geneticJsonContainsFieldsTest(TestModels.procedurePerformer, jsonProperties);
  }
}
