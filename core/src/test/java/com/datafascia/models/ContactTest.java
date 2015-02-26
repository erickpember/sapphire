// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for contact model.
 */
public class ContactTest extends ModelTestBase {
  @Test
  public <T extends Object> void testContact() throws IOException, URISyntaxException {
    Contact decoded = (Contact) geneticEncodeDecodeTest(TestModels.contact);
    assertEquals(decoded.getAddress(), TestModels.address);
    assertEquals(decoded.getName(), TestModels.name);
    assertEquals(decoded.getGender(), Gender.UNDIFFERENTIATED);
    assertEquals(decoded.getBirthDate(), TestModels.getDate());
    assertEquals(decoded.getPhoto(), TestModels.getPhoto());
    assertEquals(decoded.getOrganization(), "Test Corp.");
    assertEquals(decoded.getRelationship(), "Tester");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("relationship");
    jsonProperties.add("name");
    jsonProperties.add("address");
    jsonProperties.add("gender");
    jsonProperties.add("birthDate");
    jsonProperties.add("photo");
    jsonProperties.add("organization");
    geneticJsonContainsFieldsTest(TestModels.contact, jsonProperties);
  }
}
