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
 * Test code for the Container Element in the Specimen model.
 */
public class SpecimenContainerTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSpecimenContainer() throws IOException, URISyntaxException {
    SpecimenContainer decoded = (SpecimenContainer) geneticEncodeDecodeTest(
        TestModels.specimenContainer);

    assertEquals(decoded.getAdditiveCodeableConcept(), TestModels.codeable);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getAdditiveReferenceId(), Id.of("additiveReference"));
    assertEquals(decoded.getCapacity(), TestModels.numericQuantity);
    assertEquals(decoded.getSpecimenQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getDescription(), "description");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("additiveCodeableConcept");
    jsonProperties.add("additiveReferenceId");
    jsonProperties.add("capacity");
    jsonProperties.add("description");
    jsonProperties.add("@id");
    jsonProperties.add("specimenQuantity");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.specimenContainer, jsonProperties);
  }
}
