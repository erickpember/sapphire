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
 * Test code for the Observation model.
 */
public class ObservationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testObservation() throws IOException, URISyntaxException {
    Observation decoded = (Observation) geneticEncodeDecodeTest(TestModels.observation);
    assertEquals(decoded.getBodySiteCodeableConcept(), TestModels.codeable);
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getDataAbsentReason(), TestModels.codeable);
    assertEquals(decoded.getIdentifier(), TestModels.codeable);
    assertEquals(decoded.getInterpretation(), TestModels.codeable);
    assertEquals(decoded.getMethod(), TestModels.codeable);
    assertEquals(decoded.getName(), TestModels.codeable);
    assertEquals(decoded.getBodySiteReferenceId(), Id.of("BodySite"));
    assertEquals(decoded.getEncounterId(), Id.of("Encounter"));
    assertEquals(decoded.getId(), Id.of("Observation"));
    assertEquals(decoded.getSpecimenId(), Id.of("Specimen"));
    assertEquals(decoded.getAppliesDateTime(), TestModels.getDateTime());
    assertEquals(decoded.getIssued(), TestModels.getDateTime());
    assertEquals(decoded.getAppliesPeriod(), TestModels.period);
    assertEquals(decoded.getPerformers(), Arrays.asList(TestModels.observationPerformer));
    assertEquals(decoded.getReferenceRanges(), Arrays.asList(TestModels.observationReferenceRange));
    assertEquals(decoded.getRelated(), Arrays.asList(TestModels.observationRelated));
    assertEquals(decoded.getDevice(), TestModels.observationDevice);
    assertEquals(decoded.getReliability(), ObservationReliability.CALIBRATING);
    assertEquals(decoded.getStatus(), ObservationStatus.AMENDED);
    assertEquals(decoded.getSubject(), TestModels.observationSubject);
    assertEquals(decoded.getValue(), TestModels.observationValue);
    assertEquals(decoded.getComments(), "comments");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("appliesDateTime");
    jsonProperties.add("appliesPeriod");
    jsonProperties.add("bodySiteCodeableConcept");
    jsonProperties.add("bodySiteReferenceId");
    jsonProperties.add("code");
    jsonProperties.add("comments");
    jsonProperties.add("dataAbsentReason");
    jsonProperties.add("device");
    jsonProperties.add("encounterId");
    jsonProperties.add("@id");
    jsonProperties.add("identifier");
    jsonProperties.add("interpretation");
    jsonProperties.add("issued");
    jsonProperties.add("method");
    jsonProperties.add("name");
    jsonProperties.add("performers");
    jsonProperties.add("referenceRanges");
    jsonProperties.add("related");
    jsonProperties.add("reliability");
    jsonProperties.add("specimenId");
    jsonProperties.add("status");
    jsonProperties.add("subject");
    jsonProperties.add("value");

    geneticJsonContainsFieldsTest(TestModels.observation, jsonProperties);
  }
}
