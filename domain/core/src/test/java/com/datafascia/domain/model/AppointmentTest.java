// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Appointment model.
 */
public class AppointmentTest extends ModelTestBase {
  @Test
  public <T extends Object> void testAppointment() throws IOException, URISyntaxException {
    Appointment decoded = (Appointment) geneticEncodeDecodeTest(TestModels.appointment);

    assertEquals(decoded.getStatus(), AppointmentStatus.ARRIVED);
    assertEquals(decoded.getPriority(), new BigDecimal(9001));
    assertEquals(decoded.getReason(), TestModels.codeable);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("Appointment"));
    assertEquals(decoded.getOrderId(), Id.of("Order"));
    assertEquals(decoded.getEnd(), TestModels.getDateTime());
    assertEquals(decoded.getStart(), TestModels.getDateTime());
    assertEquals(decoded.getParticipants(), Arrays.asList(TestModels.appointmentParticipant));
    assertEquals(decoded.getSlotIds(), Arrays.asList(Id.of("Slot")));
    assertEquals(decoded.getComment(), "comment");
    assertEquals(decoded.getDescription(), "description");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("comment");
    jsonProperties.add("description");
    jsonProperties.add("end");
    jsonProperties.add("@id");
    jsonProperties.add("orderId");
    jsonProperties.add("participants");
    jsonProperties.add("priority");
    jsonProperties.add("reason");
    jsonProperties.add("slotIds");
    jsonProperties.add("start");
    jsonProperties.add("status");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.appointment, jsonProperties);
  }
}
