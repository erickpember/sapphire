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
 * Test code for PractitionerRole model.
 */
public class PractitionerRoleTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPractitionerRole() throws IOException, URISyntaxException {
    PractitionerRole decoded
        = (PractitionerRole) geneticEncodeDecodeTest(TestModels.practitionerRole);

    assertEquals(decoded.getRole(), TestModels.codeable);
    assertEquals(decoded.getSpecialty(), TestModels.codeable);
    assertEquals(decoded.getLocationId(), Id.of("location"));
    assertEquals(decoded.getManagingOrganizationId(), Id.of("managingOrganization"));
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getHealthcareServiceIds(), Arrays.asList(Id.of("healthcareService")));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("healthcareServiceIds");
    jsonProperties.add("locationId");
    jsonProperties.add("managingOrganizationId");
    jsonProperties.add("period");
    jsonProperties.add("role");
    jsonProperties.add("specialty");

    geneticJsonContainsFieldsTest(TestModels.practitionerRole, jsonProperties);
  }
}
