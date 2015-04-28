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
 * Test code for AppointmentParticipantActor model.
 */
public class AppointmentParticipantActorTest extends ModelTestBase {
  @Test
  public <T extends Object> void testAppointmentParticipantActor() throws IOException,
      URISyntaxException {
    AppointmentParticipantActor decoded = (AppointmentParticipantActor) geneticEncodeDecodeTest(
        TestModels.appointmentParticipantActor);

    assertEquals(decoded.getDeviceId(), Id.of("Device"));
    assertEquals(decoded.getHealthcareServiceId(), Id.of("HealthcareService"));
    assertEquals(decoded.getLocationId(), Id.of("Location"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
    assertEquals(decoded.getRelatedPersonId(), Id.of("RelatedPerson"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("healthcareServiceId");
    jsonProperties.add("locationId");
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");
    jsonProperties.add("relatedPersonId");

    geneticJsonContainsFieldsTest(TestModels.appointmentParticipantActor, jsonProperties);
  }
}
