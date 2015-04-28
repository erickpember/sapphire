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
 * Test code for the CareTeam Element in the EpisodeOfCare model.
 */
public class EpisodeOfCareTeamMemberTest extends ModelTestBase {
  @Test
  public <T extends Object> void testEpisodeOfCareTeamMember() throws IOException,
      URISyntaxException {
    EpisodeOfCareTeamMember decoded = (EpisodeOfCareTeamMember) geneticEncodeDecodeTest(
        TestModels.episodeOfCareTeamMember);

    assertEquals(decoded.getOrganizationMemberId(), Id.of("Organization"));
    assertEquals(decoded.getPractitionerMemberId(), Id.of("Practitioner"));
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getRoles(), Arrays.asList(TestModels.codeable));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("organizationMemberId");
    jsonProperties.add("period");
    jsonProperties.add("practitionerMemberId");
    jsonProperties.add("roles");

    geneticJsonContainsFieldsTest(TestModels.episodeOfCareTeamMember, jsonProperties);
  }
}
