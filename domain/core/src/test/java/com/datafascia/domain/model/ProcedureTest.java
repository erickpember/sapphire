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
 * Test code for the Procedure model.
 */
public class ProcedureTest extends ModelTestBase {
  @Test
  public <T extends Object> void testProcedure() throws IOException, URISyntaxException {
    Procedure decoded = (Procedure) geneticEncodeDecodeTest(TestModels.procedure);

    assertEquals(decoded.getCategory(), TestModels.codeable);
    assertEquals(decoded.getOutcome(), TestModels.codeable);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getEncounterId(), Id.of("Encounter"));
    assertEquals(decoded.getLocationId(), Id.of("Location"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getId(), Id.of("Procedure"));
    assertEquals(decoded.getPerformedDateTime(), TestModels.getDateTime());
    assertEquals(decoded.getPerformedPeriod(), TestModels.period);
    assertEquals(decoded.getComplications(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getFollowups(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getIndications(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getReportIds(), Arrays.asList(Id.of("DiagnosticReport")));
    assertEquals(decoded.getBodySites(), Arrays.asList(TestModels.procedureBodySite));
    assertEquals(decoded.getDevices(), Arrays.asList(TestModels.procedureDevice));
    assertEquals(decoded.getPerformers(), Arrays.asList(TestModels.procedurePerformer));
    assertEquals(decoded.getRelatedItems(), Arrays.asList(TestModels.procedureRelatedItem));
    assertEquals(decoded.getStatus(), ProcedureStatus.ABORTED);
    assertEquals(decoded.getUsed(), TestModels.procedureUsedItem);
    assertEquals(decoded.getNotes(), "string");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("bodySites");
    jsonProperties.add("category");
    jsonProperties.add("complications");
    jsonProperties.add("devices");
    jsonProperties.add("encounterId");
    jsonProperties.add("followups");
    jsonProperties.add("@id");
    jsonProperties.add("indications");
    jsonProperties.add("locationId");
    jsonProperties.add("notes");
    jsonProperties.add("outcome");
    jsonProperties.add("patientId");
    jsonProperties.add("performedDateTime");
    jsonProperties.add("performedPeriod");
    jsonProperties.add("performers");
    jsonProperties.add("relatedItems");
    jsonProperties.add("reportIds");
    jsonProperties.add("status");
    jsonProperties.add("type");
    jsonProperties.add("used");

    geneticJsonContainsFieldsTest(TestModels.procedure, jsonProperties);
  }
}
