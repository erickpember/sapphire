// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for the DiagnosticImage model.
 */
public class DiagnosticImageTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDiagnosticImage() throws IOException, URISyntaxException {
    DiagnosticImage decoded
        = (DiagnosticImage) geneticEncodeDecodeTest(TestModels.diagnosticImage);

    assertEquals(decoded.getLink(), TestModels.getPhoto());
    assertEquals(decoded.getComment(), "comment");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("comment");
    jsonProperties.add("link");

    geneticJsonContainsFieldsTest(TestModels.diagnosticImage, jsonProperties);
  }
}
