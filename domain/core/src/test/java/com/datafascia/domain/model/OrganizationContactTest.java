// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for OrganizationContact model.
 */
public class OrganizationContactTest extends ModelTestBase {
  @Test
  public <T extends Object> void testOrganizationContact() throws IOException, URISyntaxException {
    OrganizationContact decoded
        = (OrganizationContact) geneticEncodeDecodeTest(TestModels.organizationContact);

    assertEquals(decoded.getAddress(),TestModels.address);
    assertEquals(decoded.getTelecoms(),Arrays.asList(TestModels.contactPoint));
    assertEquals(decoded.getName(),TestModels.humanName);
    assertEquals(decoded.getPurpose(),"it passes butter");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("address");
    jsonProperties.add("name");
    jsonProperties.add("purpose");
    jsonProperties.add("telecoms");

    geneticJsonContainsFieldsTest(TestModels.organizationContact, jsonProperties);
  }
}
