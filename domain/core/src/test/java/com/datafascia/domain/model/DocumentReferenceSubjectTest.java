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
 * Test code for Subject Element in the DocumentReference model.
 */
public class DocumentReferenceSubjectTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDocumentReferenceSubject() throws IOException,
      URISyntaxException {
    DocumentReferenceSubject decoded = (DocumentReferenceSubject) geneticEncodeDecodeTest(
        TestModels.documentReferenceSubject);

    assertEquals(decoded.getDeviceId(), Id.of("Device"));
    assertEquals(decoded.getGroupId(), Id.of("Group"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("groupId");
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");

    geneticJsonContainsFieldsTest(TestModels.documentReferenceSubject, jsonProperties);
  }
}
