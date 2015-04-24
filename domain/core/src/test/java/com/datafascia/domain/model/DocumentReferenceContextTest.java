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
 * Test code for the Context element in the DocumentReference model.
 */
public class DocumentReferenceContextTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDocumentReferenceContext() throws IOException,
      URISyntaxException {
    DocumentReferenceContext decoded = (DocumentReferenceContext) geneticEncodeDecodeTest(
        TestModels.documentReferenceContext);

    assertEquals(decoded.getFacilityType(), TestModels.codeable);
    assertEquals(decoded.getPracticeSetting(), TestModels.codeable);
    assertEquals(decoded.getSourcePatientInfoId(), Id.of("Patient"));
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getEvents(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getRelated(), Arrays.asList(TestModels.getURI()));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("events");
    jsonProperties.add("facilityType");
    jsonProperties.add("period");
    jsonProperties.add("practiceSetting");
    jsonProperties.add("related");
    jsonProperties.add("sourcePatientInfoId");

    geneticJsonContainsFieldsTest(TestModels.documentReferenceContext, jsonProperties);
  }
}
