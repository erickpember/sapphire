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
 * Test code for the item Element in the DiagnosticOrder model.
 */
public class DiagnosticOrderItemTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDiagnosticOrderItem() throws IOException, URISyntaxException {
    DiagnosticOrderItem decoded
        = (DiagnosticOrderItem) geneticEncodeDecodeTest(TestModels.diagnosticOrderItem);

    assertEquals(decoded.getBodySiteCodeableConcept(), TestModels.codeable);
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getEvent(), TestModels.diagnosticOrderEvent);
    assertEquals(decoded.getStatus(), DiagnosticOrderItemStatus.ACCEPTED);
    assertEquals(decoded.getBodySiteReferenceId(), Id.of("BodySiteReference"));
    assertEquals(decoded.getSpecimens(), Arrays.asList(Id.of("Specimens")));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("bodySiteCodeableConcept");
    jsonProperties.add("bodySiteReferenceId");
    jsonProperties.add("code");
    jsonProperties.add("event");
    jsonProperties.add("specimens");
    jsonProperties.add("status");

    geneticJsonContainsFieldsTest(TestModels.diagnosticOrderItem, jsonProperties);
  }
}
