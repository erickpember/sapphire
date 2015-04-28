// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for the Participant element in Appointment model.
 */
public class AppointmentParticipantTest extends ModelTestBase {
  @Test
  public <T extends Object> void testAppointmentParticipant()
      throws IOException, URISyntaxException {
    AppointmentParticipant decoded = (AppointmentParticipant) geneticEncodeDecodeTest(
        TestModels.appointmentParticipant);

    assertEquals(decoded.getActor(), TestModels.appointmentParticipantActor);
    assertEquals(decoded.getRequired(), AppointmentParticipantRequired.INFORMATION_ONLY);
    assertEquals(decoded.getStatus(), AppointmentParticipantStatus.ACCEPTED);
    assertEquals(decoded.getType(), TestModels.codeable);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("actor");
    jsonProperties.add("required");
    jsonProperties.add("status");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.appointmentParticipant, jsonProperties);
  }
}
