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
 * Test code for BodySite model.
 */
public class BodySiteTest extends ModelTestBase {
  @Test
  public <T extends Object> void testBodySite() throws IOException, URISyntaxException {
    BodySite decoded = (BodySite) geneticEncodeDecodeTest(TestModels.bodySite);

    assertEquals(decoded.getId(), Id.of("1234"));
    assertEquals(decoded.getPatientId(), Id.of("5678"));
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getModifier(), TestModels.codeable);
    assertEquals(decoded.getImages(), Arrays.asList(TestModels.getPhoto()));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("@id");
    jsonProperties.add("patientId");
    jsonProperties.add("code");
    jsonProperties.add("modifier");
    jsonProperties.add("images");
    geneticJsonContainsFieldsTest(TestModels.bodySite, jsonProperties);
  }
}
