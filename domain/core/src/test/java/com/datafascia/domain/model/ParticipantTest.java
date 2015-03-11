// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for participant model.
 */
public class ParticipantTest extends ModelTestBase {
  @Test
  public <T extends Object> void testParticipant() throws IOException, URISyntaxException {
    Participant decoded = (Participant) geneticEncodeDecodeTest(TestModels.participant);
    assertEquals(decoded.getRole(), TestModels.codeable);
    assertEquals(decoded.getIndividual(), TestModels.getURI());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("role");
    jsonProperties.add("individual");
    geneticJsonContainsFieldsTest(TestModels.participant, jsonProperties);
  }
}