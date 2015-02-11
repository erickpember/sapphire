// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for person model.
 */
public class PersonTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPerson() throws IOException, URISyntaxException {
    Person decoded = (Person) geneticEncodeDecodeTest(TestModels.person);
    assertEquals(decoded.getName(), TestModels.name);
    assertEquals(decoded.getAddress(), TestModels.address);
    assertEquals(decoded.getGender(), Gender.Male);
    assertEquals(decoded.getBirthDate(), TestModels.getDate());
    assertEquals(decoded.getPhoto(), TestModels.getURI());
    assertEquals(decoded.getOrganization(), "Some Ficticious Hospital");
  }
}
