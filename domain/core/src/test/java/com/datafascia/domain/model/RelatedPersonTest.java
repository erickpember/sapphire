// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for RelatedPerson model.
 */
public class RelatedPersonTest extends ModelTestBase {
  @Test
  public void testContact() throws Exception {
    RelatedPerson decoded = (RelatedPerson) geneticEncodeDecodeTest(TestModels.relatedPerson);
    assertEquals(decoded.getGender(), Gender.FEMALE);
    assertEquals(decoded.getManagingOrganizationId(), Id.of("Organization"));
    assertEquals(decoded.getPhoto(), TestModels.getPhoto());
    assertEquals(decoded.getAddresses(), Arrays.asList(TestModels.address));
    assertEquals(decoded.getTelecoms(), Arrays.asList(TestModels.contactPoint));
    assertEquals(decoded.getNames(), Arrays.asList(TestModels.humanName));
    assertEquals(decoded.getLinks(), Arrays.asList(TestModels.personLink));
    assertEquals(decoded.getBirthDate(), TestModels.getDate());
    assertEquals(decoded.getRelationship(), TestModels.codeable);
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getId(), Id.of("RelatedPerson"));
  }

  @Test
  public void testJsonProperties() throws Exception {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("addresses");
    jsonProperties.add("birthDate");
    jsonProperties.add("gender");
    jsonProperties.add("links");
    jsonProperties.add("managingOrganizationId");
    jsonProperties.add("names");
    jsonProperties.add("photo");
    jsonProperties.add("telecoms");
    jsonProperties.add("@id");
    jsonProperties.add("patientId");
    jsonProperties.add("relationship");

    geneticJsonContainsFieldsTest(TestModels.relatedPerson, jsonProperties);
  }
}
