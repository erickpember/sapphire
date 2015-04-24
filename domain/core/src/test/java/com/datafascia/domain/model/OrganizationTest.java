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
 * Test code for Organization model.
 */
public class OrganizationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testOrganization() throws IOException, URISyntaxException {
    Organization decoded = (Organization) geneticEncodeDecodeTest(TestModels.organization);

    assertTrue(decoded.getActive());
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getPartOfId(), Id.of("partOf"));
    assertEquals(decoded.getAddresses(), Arrays.asList(TestModels.address));
    assertEquals(decoded.getTelecoms(), Arrays.asList(TestModels.contactPoint));
    assertEquals(decoded.getLocationIds(), Arrays.asList(Id.of("location")));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("active");
    jsonProperties.add("addresses");
    jsonProperties.add("@id");
    jsonProperties.add("locationIds");
    jsonProperties.add("name");
    jsonProperties.add("partOfId");
    jsonProperties.add("telecoms");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.organization, jsonProperties);
  }
}
