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
 * Test code for DiagnosticReport model.
 */
public class DiagnosticReportTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDiagnosticReport() throws IOException, URISyntaxException {
    DiagnosticReport decoded = (DiagnosticReport) geneticEncodeDecodeTest(
        TestModels.diagnosticReport);

    assertEquals(decoded.getCodedDiagnoses(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getName(), TestModels.codeable);
    assertEquals(decoded.getServiceCategory(), TestModels.codeable);
    assertEquals(decoded.getPerformer(), TestModels.diagnosticPerformer);
    assertEquals(decoded.getStatus(), DiagnosticReportStatus.APPENDED);
    assertEquals(decoded.getSubject(), TestModels.diagnosticSubject);
    assertEquals(decoded.getId(), Id.of("DiagnosticReport"));
    assertEquals(decoded.getEncounterId(), Id.of("Encounter"));
    assertEquals(decoded.getDiagnosticDateTime(), TestModels.getDateTime());
    assertEquals(decoded.getIssued(), TestModels.getDateTime());
    assertEquals(decoded.getDiagnosticPeriod(), TestModels.period);
    assertEquals(decoded.getPresentedForms(), Arrays.asList(TestModels.attachment));
    assertEquals(decoded.getImages(), Arrays.asList(TestModels.diagnosticImage));
    assertEquals(decoded.getRequestDetailIds(), Arrays.asList(Id.of("RequestDetails")));
    assertEquals(decoded.getImagingStudy(), Arrays.asList(TestModels.imagingStudy));
    assertEquals(decoded.getSpecimens(), Arrays.asList(TestModels.specimen));
    assertEquals(decoded.getResultId(), Id.of("Result"));
    assertEquals(decoded.getConclusion(), "conclusion");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("codedDiagnoses");
    jsonProperties.add("conclusion");
    jsonProperties.add("diagnosticDateTime");
    jsonProperties.add("diagnosticPeriod");
    jsonProperties.add("encounterId");
    jsonProperties.add("@id");
    jsonProperties.add("images");
    jsonProperties.add("imagingStudy");
    jsonProperties.add("issued");
    jsonProperties.add("name");
    jsonProperties.add("performer");
    jsonProperties.add("presentedForms");
    jsonProperties.add("requestDetailIds");
    jsonProperties.add("resultId");
    jsonProperties.add("serviceCategory");
    jsonProperties.add("specimens");
    jsonProperties.add("status");
    jsonProperties.add("subject");

    geneticJsonContainsFieldsTest(TestModels.diagnosticReport, jsonProperties);
  }
}
