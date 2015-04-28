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
import static org.testng.Assert.assertTrue;

/**
 * Test code for Person model.
 */
public class PersonTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPerson() throws IOException, URISyntaxException {
    Person decoded = (Person) geneticEncodeDecodeTest(TestModels.person);
    assertTrue(decoded.isActive());
    assertEquals(decoded.getGender(), Gender.FEMALE);
    assertEquals(decoded.getManagingOrganizationId(), Id.of("Organization"));
    assertEquals(decoded.getPhoto(), TestModels.getPhoto());
    assertEquals(decoded.getAddresses(), Arrays.asList(TestModels.address));
    assertEquals(decoded.getTelecoms(), Arrays.asList(TestModels.contactPoint));
    assertEquals(decoded.getNames(), Arrays.asList(TestModels.humanName));
    assertEquals(decoded.getLinks(), Arrays.asList(TestModels.personLink));
    assertEquals(decoded.getBirthDate(), TestModels.getDate());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("active");
    jsonProperties.add("addresses");
    jsonProperties.add("birthDate");
    jsonProperties.add("gender");
    jsonProperties.add("links");
    jsonProperties.add("managingOrganizationId");
    jsonProperties.add("names");
    jsonProperties.add("photo");
    jsonProperties.add("telecoms");
    geneticJsonContainsFieldsTest(TestModels.person, jsonProperties);
  }
}
