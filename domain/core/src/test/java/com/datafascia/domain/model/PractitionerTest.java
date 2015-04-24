// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.neovisionaries.i18n.LanguageCode;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Practitioner model.
 */
public class PractitionerTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPractitioner() throws IOException, URISyntaxException {
    Practitioner decoded = (Practitioner) geneticEncodeDecodeTest(TestModels.practitioner);

    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getPractitionerRoles(), Arrays.asList(TestModels.practitionerRole));
    assertEquals(decoded.getTelecoms(), Arrays.asList(TestModels.contactPoint));
    assertEquals(decoded.getCommunications(), Arrays.asList(LanguageCode.es));
    assertEquals(decoded.getQualifications(), Arrays.asList(TestModels.qualification));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("communications");
    jsonProperties.add("@id");
    jsonProperties.add("practitionerRoles");
    jsonProperties.add("qualifications");
    jsonProperties.add("telecoms");

    geneticJsonContainsFieldsTest(TestModels.practitioner, jsonProperties);
  }
}
