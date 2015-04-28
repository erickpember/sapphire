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
 * Test code for ReferralRequestRequester model.
 */
public class ReferralRequestRequesterTest extends ModelTestBase {
  @Test
  public <T extends Object> void testReferralRequestRequester() throws IOException,
      URISyntaxException {
    ReferralRequestRequester decoded = (ReferralRequestRequester) geneticEncodeDecodeTest(
        TestModels.referralRequestRequester);

    assertEquals(decoded.getOrganizationRequesterId(), Id.of("Organization"));
    assertEquals(decoded.getPatientRequesterId(), Id.of("Patient"));
    assertEquals(decoded.getPractitionerRequesterId(), Id.of("Practitioner"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("organizationRequesterId");
    jsonProperties.add("patientRequesterId");
    jsonProperties.add("practitionerRequesterId");

    geneticJsonContainsFieldsTest(TestModels.referralRequestRequester, jsonProperties);
  }
}
