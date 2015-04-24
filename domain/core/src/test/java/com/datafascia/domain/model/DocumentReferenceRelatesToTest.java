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
 * Test code for RelatesTo Element in the DocumentReference model.
 */
public class DocumentReferenceRelatesToTest extends ModelTestBase {
  @Test
  public <T extends Object> void testDocumentReferenceRelatesTo() throws IOException,
      URISyntaxException {
    DocumentReferenceRelatesTo decoded = (DocumentReferenceRelatesTo) geneticEncodeDecodeTest(
        TestModels.documentReferenceRelatesTo);

    assertEquals(decoded.getCode(), DocumentRelationshipType.APPENDS);
    assertEquals(decoded.getTargetId(), Id.of("Target"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("code");
    jsonProperties.add("targetId");

    geneticJsonContainsFieldsTest(TestModels.documentReferenceRelatesTo, jsonProperties);
  }
}
