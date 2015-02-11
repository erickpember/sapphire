// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
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
    assertEquals(decoded.getGender(), Gender.Undifferentiated);
    assertEquals(decoded.getBirthDate(), TestModels.getDate());
    assertEquals(decoded.getPhoto(), TestModels.getURI());
    assertEquals(decoded.getOrganization(), "Test Corp.");
    assertEquals(decoded.getRelationship(), "Tester");
  }
}
