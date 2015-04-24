// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for DiagnosticOrderEvent Element in the DiagnosticOrder model.
 */
public class DiagnosticOrderEventTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDiagnosticOrderEvent() throws IOException, URISyntaxException {
    DiagnosticOrderEvent decoded
        = (DiagnosticOrderEvent) geneticEncodeDecodeTest(TestModels.diagnosticOrderEvent);

    assertEquals(decoded.getDescription(), TestModels.codeable);
    assertEquals(decoded.getActor(), TestModels.diagnosticOrderEventActor);
    assertEquals(decoded.getStatus(), DiagnosticOrderEventStatus.ACCEPTED);
    assertEquals(decoded.getDateTime(), TestModels.getDateTime());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("actor");
    jsonProperties.add("dateTime");
    jsonProperties.add("description");
    jsonProperties.add("status");

    geneticJsonContainsFieldsTest(TestModels.diagnosticOrderEvent, jsonProperties);
  }
}
