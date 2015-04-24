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
 * Test code for the Member Element in the Group model.
 */
public class GroupMemberTest extends ModelTestBase {
  @Test
  public <T extends Object> void testGroupMember() throws IOException, URISyntaxException {
    GroupMember decoded = (GroupMember) geneticEncodeDecodeTest(TestModels.groupMember);

    assertEquals(decoded.getDeviceId(), Id.of("Device"));
    assertEquals(decoded.getMedicationId(), Id.of("Medication"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
    assertEquals(decoded.getSubstanceId(), Id.of("Substance"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("medicationId");
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");
    jsonProperties.add("substanceId");

    geneticJsonContainsFieldsTest(TestModels.groupMember, jsonProperties);
  }
}
