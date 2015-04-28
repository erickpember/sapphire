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
 * Test code for EpisodeOfCare model.
 */
public class EpisodeOfCareTest extends ModelTestBase {
  @Test
  public <T extends Object> void testEpisodeOfCare() throws IOException, URISyntaxException {
    EpisodeOfCare decoded = (EpisodeOfCare) geneticEncodeDecodeTest(TestModels.episodeOfCare);

    assertEquals(decoded.getStatus(), EpisodeOfCareStatus.ACTIVE);
    assertEquals(decoded.getId(), Id.of("EpisodeOfCare"));
    assertEquals(decoded.getManagingOrganizationId(), Id.of("Organization"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getCareManagerId(), Id.of("Practitioner"));
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getTypes(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getStatusHistory(), Arrays.asList(EpisodeOfCareStatus.ACTIVE));
    assertEquals(decoded.getCareTeam(), Arrays.asList(TestModels.episodeOfCareTeamMember));
    assertEquals(decoded.getConditionIds(), Arrays.asList(Id.of("Condition")));
    assertEquals(decoded.getReferralRequestIds(), Arrays.asList(Id.of("ReferralRequest")));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("careManagerId");
    jsonProperties.add("careTeam");
    jsonProperties.add("conditionIds");
    jsonProperties.add("@id");
    jsonProperties.add("managingOrganizationId");
    jsonProperties.add("patientId");
    jsonProperties.add("period");
    jsonProperties.add("referralRequestIds");
    jsonProperties.add("status");
    jsonProperties.add("statusHistory");
    jsonProperties.add("types");

    geneticJsonContainsFieldsTest(TestModels.episodeOfCare, jsonProperties);
  }
}
