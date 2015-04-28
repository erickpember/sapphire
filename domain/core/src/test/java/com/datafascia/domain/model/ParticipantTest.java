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
 * Test code for the Participant Element of the Encounter model.
 */
public class ParticipantTest extends ModelTestBase {
  @Test
  public <T extends Object> void testParticipant() throws IOException, URISyntaxException {
    Participant decoded = (Participant) geneticEncodeDecodeTest(TestModels.participant);
    assertEquals(decoded.getIndividualPractitionerId(), Id.of("IndividualPractitioner"));
    assertEquals(decoded.getIndividualRelatedPersonId(), Id.of("IndividualRelatedPerson"));
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getTypes(), Arrays.asList(TestModels.codeable));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("individualPractitionerId");
    jsonProperties.add("individualRelatedPersonId");
    jsonProperties.add("period");
    jsonProperties.add("types");

    geneticJsonContainsFieldsTest(TestModels.participant, jsonProperties);
  }
}
