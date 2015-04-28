// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

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

    assertEquals(decoded.getLength(), TestModels.duration);
    assertEquals(decoded.getEClass(), EncounterClass.AMBULATORY);
    assertEquals(decoded.getPriority(), EncounterPriority.Emergency);
    assertEquals(decoded.getStatus(), EncounterStatus.ARRIVED);
    assertEquals(decoded.getHospitalization(), TestModels.hospitalization);
    assertEquals(decoded.getFulfillsId(), Id.of("Appointment"));
    assertEquals(decoded.getId(), Id.of("Encounter"));
    assertEquals(decoded.getPartOfId(), Id.of("Encounter"));
    assertEquals(decoded.getEpisodeOfCareId(), Id.of("EpisodeOfCare"));
    assertEquals(decoded.getServiceProviderId(), Id.of("Organization"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getReasons(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getLocations(), Arrays.asList(TestModels.encounterLocation));
    assertEquals(decoded.getStatusHistory(), Arrays.asList(TestModels.encounterStatusHistory));
    assertEquals(decoded.getTypes(), Arrays.asList(EncounterType.ADMS));
    assertEquals(decoded.getIncomingReferralRequestIds(), Arrays.asList(Id.of("EpisodeOfCare")));
    assertEquals(decoded.getParticipants(), Arrays.asList(TestModels.participant));
    assertEquals(decoded.getIndications(), Arrays.asList(TestModels.reference));
    assertEquals(decoded.getIdentifier(), "identifier");
    assertEquals(decoded.getObservations(),Arrays.asList(TestModels.observation));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("eClass");
    jsonProperties.add("episodeOfCare");
    jsonProperties.add("fulfillsId");
    jsonProperties.add("hospitalization");
    jsonProperties.add("@id");
    jsonProperties.add("identifier");
    jsonProperties.add("incomingReferralRequestIds");
    jsonProperties.add("indications");
    jsonProperties.add("length");
    jsonProperties.add("locations");
    jsonProperties.add("participants");
    jsonProperties.add("partOfId");
    jsonProperties.add("patientId");
    jsonProperties.add("period");
    jsonProperties.add("priority");
    jsonProperties.add("reasons");
    jsonProperties.add("serviceProviderId");
    jsonProperties.add("status");
    jsonProperties.add("statusHistory");
    jsonProperties.add("types");
    jsonProperties.add("observations");

    geneticJsonContainsFieldsTest(TestModels.encounter, jsonProperties);
  }
}
