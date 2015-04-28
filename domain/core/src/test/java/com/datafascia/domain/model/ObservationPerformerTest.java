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
 * Test code for the Performers Element in the Observation model.
 */
public class ObservationPerformerTest extends ModelTestBase {
  @Test
  public <T extends Object> void testObservationPerformer() throws IOException, URISyntaxException {
    ObservationPerformer decoded = (ObservationPerformer) geneticEncodeDecodeTest(
        TestModels.observationPerformer);

    assertEquals(decoded.getOrganizationId(), Id.of("Organization"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("organizationId");
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");

    geneticJsonContainsFieldsTest(TestModels.observationPerformer, jsonProperties);
  }
}
