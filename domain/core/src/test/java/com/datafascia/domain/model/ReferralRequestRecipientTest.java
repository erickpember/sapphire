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
 * Test code for the Recipients Element of the ReferralRequest model.
 */
public class ReferralRequestRecipientTest extends ModelTestBase {
  @Test
  public <T extends Object> void testReferralRequestRecipient() throws IOException,
      URISyntaxException {
    ReferralRequestRecipient decoded = (ReferralRequestRecipient) geneticEncodeDecodeTest(
        TestModels.referralRequestRecipient);

    assertEquals(decoded.getOrganizationRecipientId(), Id.of("Organization"));
    assertEquals(decoded.getPractitionerRecipientId(), Id.of("Practitioner"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("organizationRecipientId");
    jsonProperties.add("practitionerRecipientId");

    geneticJsonContainsFieldsTest(TestModels.referralRequestRecipient, jsonProperties);
  }
}
