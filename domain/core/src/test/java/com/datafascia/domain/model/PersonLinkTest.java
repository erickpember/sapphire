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
 * Test code for the Links Element in the Person model.
 */
public class PersonLinkTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPersonLink() throws IOException, URISyntaxException {
    PersonLink decoded = (PersonLink) geneticEncodeDecodeTest(TestModels.personLink);

    assertEquals(decoded.getTargetPatientId(), Id.of("Patient"));
    assertEquals(decoded.getTargetPractitionerId(), Id.of("Practitioner"));
    assertEquals(decoded.getTargetRelatedPersonId(), Id.of("RelatedPerson"));
    assertEquals(decoded.getAssurance(), PersonLinkAssurance.LEVEL1);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("assurance");
    jsonProperties.add("targetPatientId");
    jsonProperties.add("targetPractitionerId");
    jsonProperties.add("targetRelatedPersonId");

    geneticJsonContainsFieldsTest(TestModels.personLink, jsonProperties);
  }
}
