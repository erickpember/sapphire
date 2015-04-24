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
 * Test code for the Subject Element in the Specimen model.
 */
public class SpecimenSubjectTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSpecimenSubject() throws IOException, URISyntaxException {
    SpecimenSubject decoded = (SpecimenSubject) geneticEncodeDecodeTest(TestModels.specimenSubject);

    assertEquals(decoded.getDeviceId(), Id.of("Device"));
    assertEquals(decoded.getGroupId(), Id.of("Group"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getSubstanceId(), Id.of("Substance"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("deviceId");
    jsonProperties.add("groupId");
    jsonProperties.add("patientId");
    jsonProperties.add("substanceId");

    geneticJsonContainsFieldsTest(TestModels.specimenSubject, jsonProperties);
  }
}
