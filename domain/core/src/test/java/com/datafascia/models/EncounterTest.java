// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for the Encounter model.
 */
@Slf4j
public class EncounterTest extends ModelTestBase {
  @Test
  public <T extends Object> void testEncounter() throws IOException, URISyntaxException {
    Encounter decoded = (Encounter) geneticEncodeDecodeTest(TestModels.encounter);
    assertEquals(decoded.getId(), Id.of("1234"));
    assertEquals(decoded.getStatus(), EncounterStatus.InProgress);
    assertEquals(decoded.getEclass(), EncounterClass.Ambulatory);
    assertEquals(decoded.getType(), EncounterType.OKI);
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getReason(), TestModels.codeable);
    assertEquals(decoded.getIndication(), TestModels.getURI());
    assertEquals(decoded.getPriority(), EncounterPriority.SemiUrgent);
    assertEquals(decoded.getServiceProvider(), TestModels.getURI());
    assertEquals(decoded.getHospitalisation(), TestModels.hospitalization);
    assertEquals(decoded.getLocation(), Arrays.asList(TestModels.location, TestModels.location));
    assertEquals(decoded.getParticipants(),
        Arrays.asList(TestModels.participant, TestModels.participant));
    assertEquals(decoded.getLinkedTo(), TestModels.getURI());
    assertEquals(decoded.getObservations(),
        Arrays.asList(TestModels.observation, TestModels.observation));
    assertEquals(decoded.getPatient(), TestModels.getURI());
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("@id");
    jsonProperties.add("status");
    jsonProperties.add("class");
    jsonProperties.add("type");
    jsonProperties.add("period");
    jsonProperties.add("reason");
    jsonProperties.add("indication");
    jsonProperties.add("priority");
    jsonProperties.add("serviceProvider");
    jsonProperties.add("hospitalisation");
    jsonProperties.add("location");
    jsonProperties.add("participants");
    jsonProperties.add("linkedTo");
    jsonProperties.add("observations");
    jsonProperties.add("patient");
    geneticJsonContainsFieldsTest(TestModels.encounter, jsonProperties);
  }
}
