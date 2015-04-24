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
 * Test code for the Treatment Element in the Specimen model.
 */
public class SpecimenTreatmentTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSpecimenTreatment() throws IOException, URISyntaxException {
    SpecimenTreatment decoded = (SpecimenTreatment) geneticEncodeDecodeTest(
        TestModels.specimenTreatment);

    assertEquals(decoded.getProcedure(), TestModels.codeable);
    assertEquals(decoded.getAdditives(), Arrays.asList(Id.of("additives")));
    assertEquals(decoded.getDescription(), "description");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("additives");
    jsonProperties.add("description");
    jsonProperties.add("procedure");

    geneticJsonContainsFieldsTest(TestModels.specimenTreatment, jsonProperties);
  }
}
