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
 * Test code for condition model.
 */
public class ConditionTest extends ModelTestBase {
  @Test
  public <T extends Object> void testCondition() throws IOException, URISyntaxException {
    Condition decoded = (Condition) geneticEncodeDecodeTest(TestModels.condition);

    assertEquals(decoded.getCategory(), TestModels.codeable);
    assertEquals(decoded.getCertainty(), TestModels.codeable);
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getSeverity(), TestModels.codeable);
    assertEquals(decoded.getClinicalStatus(), ConditionClinicalStatus.REFUTED);
    assertEquals(decoded.getOnset(), TestModels.conditionOnset);
    assertEquals(decoded.getStage(), TestModels.conditionStage);
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getEncounterId(), Id.of("encounter"));
    assertEquals(decoded.getPatientId(), Id.of("patient"));
    assertEquals(decoded.getAsserterId(), Id.of("asserter"));
    assertEquals(decoded.getDateWritten(), TestModels.getDateTime());
    assertEquals(decoded.getDueTo(), Arrays.asList(TestModels.conditionDueTo));
    assertEquals(decoded.getEvidence(), Arrays.asList(TestModels.conditionEvidence));
    assertEquals(decoded.getLocations(), Arrays.asList(TestModels.conditionLocation));
    assertEquals(decoded.getOccurredFollowing(),
        Arrays.asList(TestModels.conditionOccurredFollowing));
    assertEquals(decoded.getDateAsserted(), TestModels.getDate());
    assertEquals(decoded.getNotes(), "notes");
    assertEquals(decoded.getAbatement(), TestModels.conditionAbatement);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("abatement");
    jsonProperties.add("asserterId");
    jsonProperties.add("category");
    jsonProperties.add("certainty");
    jsonProperties.add("clinicalStatus");
    jsonProperties.add("code");
    jsonProperties.add("dateAsserted");
    jsonProperties.add("dateWritten");
    jsonProperties.add("dueTo");
    jsonProperties.add("encounterId");
    jsonProperties.add("evidence");
    jsonProperties.add("@id");
    jsonProperties.add("locations");
    jsonProperties.add("notes");
    jsonProperties.add("occurredFollowing");
    jsonProperties.add("onset");
    jsonProperties.add("patientId");
    jsonProperties.add("severity");
    jsonProperties.add("stage");

    geneticJsonContainsFieldsTest(TestModels.condition, jsonProperties);
  }
}
