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
 * Test code for ReferralRequest model.
 */
public class ReferralRequestTest extends ModelTestBase {
  @Test
  public <T extends Object> void testReferralRequest() throws IOException, URISyntaxException {
    ReferralRequest decoded = (ReferralRequest) geneticEncodeDecodeTest(TestModels.referralRequest);

    assertEquals(decoded.getPriority(), TestModels.codeable);
    assertEquals(decoded.getReason(), TestModels.codeable);
    assertEquals(decoded.getSpecialty(), TestModels.codeable);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getEncounterId(), Id.of("Encounter"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getId(), Id.of("ReferralRequest"));
    assertEquals(decoded.getDateSent(), TestModels.getDateTime());
    assertEquals(decoded.getFulfillmentTime(), TestModels.period);
    assertEquals(decoded.getServicesRequested(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getSupportingInformation(), Arrays.asList(TestModels.reference));
    assertEquals(decoded.getRecipients(), Arrays.asList(TestModels.referralRequestRecipient));
    assertEquals(decoded.getRequester(), TestModels.referralRequestRequester);
    assertEquals(decoded.getStatus(), ReferralRequestStatus.ACCEPTED);
    assertEquals(decoded.getDescription(), "description");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("dateSent");
    jsonProperties.add("description");
    jsonProperties.add("encounterId");
    jsonProperties.add("fulfillmentTime");
    jsonProperties.add("@id");
    jsonProperties.add("patientId");
    jsonProperties.add("priority");
    jsonProperties.add("reason");
    jsonProperties.add("recipients");
    jsonProperties.add("requester");
    jsonProperties.add("servicesRequested");
    jsonProperties.add("specialty");
    jsonProperties.add("status");
    jsonProperties.add("supportingInformation");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.referralRequest, jsonProperties);
  }
}
