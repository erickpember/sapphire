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
 * Test code for Author element of the DocumentReference model.
 */
public class DocumentReferenceAuthorTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDocumentReferenceAuthor() throws IOException,
      URISyntaxException {
    DocumentReferenceAuthor decoded = (DocumentReferenceAuthor) geneticEncodeDecodeTest(
        TestModels.documentReferenceAuthor);

    assertEquals(decoded.getDeviceId(), Id.of("Device"));
    assertEquals(decoded.getOrganizationId(), Id.of("Organization"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
    assertEquals(decoded.getRelatedPersonId(), Id.of("RelatedPerson"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("organizationId");
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");
    jsonProperties.add("relatedPersonId");

    geneticJsonContainsFieldsTest(TestModels.documentReferenceAuthor, jsonProperties);
  }
}
