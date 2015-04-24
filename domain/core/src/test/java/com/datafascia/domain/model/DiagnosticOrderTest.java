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
 * Test code for DiagnosticOrder model.
 */
public class DiagnosticOrderTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDiagnosticOrder() throws IOException, URISyntaxException {
    DiagnosticOrder decoded = (DiagnosticOrder) geneticEncodeDecodeTest(TestModels.diagnosticOrder);

    assertEquals(decoded.getPriority(), DiagnosticOrderPriority.ASAP);
    assertEquals(decoded.getStatus(), DiagnosticOrderStatus.ACCEPTED);
    assertEquals(decoded.getSubject(), TestModels.diagnosticOrderSubject);
    assertEquals(decoded.getSupportingInformation(),
        TestModels.diagnosticOrderSupportingInformation);
    assertEquals(decoded.getId(), Id.of("DiagnosticOrder"));
    assertEquals(decoded.getEncounterId(), Id.of("Encounter"));
    assertEquals(decoded.getOrdererId(), Id.of("Practitioner"));
    assertEquals(decoded.getEvents(), Arrays.asList(TestModels.diagnosticOrderEvent));
    assertEquals(decoded.getItems(), Arrays.asList(TestModels.diagnosticOrderItem));
    assertEquals(decoded.getSpecimenIds(), Arrays.asList(Id.of("Specimen")));
    assertEquals(decoded.getClinicalNotes(), "clinicalNotes");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("clinicalNotes");
    jsonProperties.add("encounterId");
    jsonProperties.add("events");
    jsonProperties.add("@id");
    jsonProperties.add("items");
    jsonProperties.add("ordererId");
    jsonProperties.add("priority");
    jsonProperties.add("specimenIds");
    jsonProperties.add("status");
    jsonProperties.add("subject");
    jsonProperties.add("supportingInformation");

    geneticJsonContainsFieldsTest(TestModels.diagnosticOrder, jsonProperties);
  }
}
