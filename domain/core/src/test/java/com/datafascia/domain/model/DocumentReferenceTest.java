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
 * Test code for DocumentReference model.
 */
public class DocumentReferenceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDocumentReference() throws IOException, URISyntaxException {
    DocumentReference decoded = (DocumentReference) geneticEncodeDecodeTest(
        TestModels.documentReference);

    assertEquals(decoded.getConfidentiality(), TestModels.codeable);
    assertEquals(decoded.getDocumentReferenceClass(), TestModels.codeable);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getAuthenticator(), TestModels.documentReferenceAuthenticator);
    assertEquals(decoded.getContext(), TestModels.documentReferenceContext);
    assertEquals(decoded.getStatus(), DocumentReferenceStatus.CURRENT);
    assertEquals(decoded.getSubject(), TestModels.documentReferenceSubject);
    assertEquals(decoded.getId(), Id.of("DocumentReference"));
    assertEquals(decoded.getCustodianId(), Id.of("Organization"));
    assertEquals(decoded.getCreated(), TestModels.getDateTime());
    assertEquals(decoded.getIndexed(), TestModels.getDateTime());
    assertEquals(decoded.getContents(), Arrays.asList(TestModels.attachment));
    assertEquals(decoded.getAuthors(), Arrays.asList(TestModels.documentReferenceAuthor));
    assertEquals(decoded.getRelatesTo(), Arrays.asList(TestModels.documentReferenceRelatesTo));
    assertEquals(decoded.getFormats(), Arrays.asList(TestModels.getURI()));
    assertEquals(decoded.getDocStatus(), ReferredDocumentStatus.AMENDED);
    assertEquals(decoded.getDescription(), "description");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("authenticator");
    jsonProperties.add("authors");
    jsonProperties.add("confidentiality");
    jsonProperties.add("contents");
    jsonProperties.add("context");
    jsonProperties.add("created");
    jsonProperties.add("custodianId");
    jsonProperties.add("description");
    jsonProperties.add("docStatus");
    jsonProperties.add("documentReferenceClass");
    jsonProperties.add("formats");
    jsonProperties.add("@id");
    jsonProperties.add("indexed");
    jsonProperties.add("relatesTo");
    jsonProperties.add("status");
    jsonProperties.add("subject");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.documentReference, jsonProperties);
  }
}
