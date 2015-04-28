// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.neovisionaries.i18n.LanguageCode;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for attachment model.
 */
public class AttachmentTest extends ModelTestBase {
  @Test
  public <T extends Object> void testAttachment() throws IOException, URISyntaxException {
    Attachment decoded = (Attachment) geneticEncodeDecodeTest(TestModels.attachment);
    assertEquals(decoded.getContentType(), "UTF-8");
    assertEquals(decoded.getLanguage(), LanguageCode.en);
    assertEquals(decoded.getData(), "test text".getBytes());
    assertEquals(decoded.getUrl(), TestModels.getURI());
    assertEquals(decoded.getTitle(), "test text");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("contentType");
    jsonProperties.add("language");
    jsonProperties.add("creation");
    jsonProperties.add("size");
    jsonProperties.add("hash");
    jsonProperties.add("data");
    jsonProperties.add("url");
    jsonProperties.add("title");
    geneticJsonContainsFieldsTest(TestModels.attachment, jsonProperties);
  }
}
