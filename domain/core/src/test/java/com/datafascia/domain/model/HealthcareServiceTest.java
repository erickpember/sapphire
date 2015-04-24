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
import static org.testng.Assert.assertFalse;

/**
 * Test code for HealthcareService model.
 */
public class HealthcareServiceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testHealthcareService() throws IOException, URISyntaxException {
    HealthcareService decoded
        = (HealthcareService) geneticEncodeDecodeTest(TestModels.healthcareService);

    //   assertEquals(decoded.getCategory(), TestModels.codeable);
    assertFalse(decoded.getAppointmentRequired());
    assertEquals(decoded.getEligibility(), TestModels.codeable);
    assertEquals(decoded.getServiceCategory(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getLocationId(), Id.of("location"));
    assertEquals(decoded.getProvidedById(), Id.of("providedBy"));
    assertEquals(decoded.getPhoto(), TestModels.getPhoto());
    assertEquals(decoded.getCharacteristic(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getReferralMethods(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getServiceProvisionCodes(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getTelecoms(), Arrays.asList(TestModels.contactPoint));
    assertEquals(decoded.getAvailableTimes(),
        Arrays.asList(TestModels.healthcareServiceAvailableTime));
    assertEquals(decoded.getNotAvailableTimes(),
        Arrays.asList(TestModels.healthcareServiceNotAvailable));
    assertEquals(decoded.getServiceTypes(), Arrays.asList(TestModels.healthcareServiceType));
    assertEquals(decoded.getProgramNames(), Arrays.asList("rub some dirt on it care"));
    assertEquals(decoded.getAvailabilityExceptions(), "not on my lunch break");
    assertEquals(decoded.getComment(), "don't flush paper towels");
    assertEquals(decoded.getEligibilityNote(), "dogs aren't eligible");
    assertEquals(decoded.getExtraDetails(), "except for puppies");
    assertEquals(decoded.getPublicKey(), "cEvin");
    assertEquals(decoded.getServiceName(), "secret");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("appointmentRequired");
    jsonProperties.add("availabilityExceptions");
    jsonProperties.add("availableTimes");
    jsonProperties.add("characteristic");
    jsonProperties.add("comment");
    jsonProperties.add("eligibility");
    jsonProperties.add("eligibilityNote");
    jsonProperties.add("extraDetails");
    jsonProperties.add("@id");
    jsonProperties.add("locationId");
    jsonProperties.add("notAvailableTimes");
    jsonProperties.add("photo");
    jsonProperties.add("programNames");
    jsonProperties.add("providedById");
    jsonProperties.add("publicKey");
    jsonProperties.add("referralMethods");
    jsonProperties.add("serviceCategory");
    jsonProperties.add("serviceName");
    jsonProperties.add("serviceProvisionCodes");
    jsonProperties.add("serviceTypes");
    jsonProperties.add("telecoms");

    geneticJsonContainsFieldsTest(TestModels.healthcareService, jsonProperties);
  }
}
