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
 * Test code for the Authenticator Element in the DocumentReference model.
 */
public class DocumentReferenceAuthenticatorTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDocumentReferenceAuthenticator() throws IOException,
      URISyntaxException {
    DocumentReferenceAuthenticator decoded
        = (DocumentReferenceAuthenticator) geneticEncodeDecodeTest(
            TestModels.documentReferenceAuthenticator);

    assertEquals(decoded.getOrganizationId(), Id.of("Organization"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("organizationId");
    jsonProperties.add("practitionerId");

    geneticJsonContainsFieldsTest(TestModels.documentReferenceAuthenticator, jsonProperties);
  }
}
