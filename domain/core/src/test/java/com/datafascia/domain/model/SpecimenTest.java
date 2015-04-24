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
 * Test code for the Specimen model.
 */
public class SpecimenTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSpecimen() throws IOException, URISyntaxException {
    Specimen decoded = (Specimen) geneticEncodeDecodeTest(TestModels.specimen);

    assertEquals(decoded.getCollectionBodySiteCodableConcept(), TestModels.codeable);
    assertEquals(decoded.getCollectionMethod(), TestModels.codeable);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getCollectionBodySiteReferenceId(), Id.of("BodySite"));
    assertEquals(decoded.getCollectorId(), Id.of("Practitioner"));
    assertEquals(decoded.getId(), Id.of("Specimen"));
    assertEquals(decoded.getCollectedDateTime(), TestModels.getDateTime());
    assertEquals(decoded.getReceivedTime(), TestModels.getDateTime());
    assertEquals(decoded.getCollectedPeriod(), TestModels.period);
    assertEquals(decoded.getContainers(), Arrays.asList(TestModels.specimenContainer));
    assertEquals(decoded.getParentIds(), Arrays.asList(Id.of("parents")));
    assertEquals(decoded.getCollectionComments(), Arrays.asList("collectionComments"));
    assertEquals(decoded.getCollectedQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getSubject(), TestModels.specimenSubject);
    assertEquals(decoded.getTreatments(), TestModels.specimenTreatment);
    assertEquals(decoded.getAccessionIdentifier(), "accessionIdentifier");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("accessionIdentifier");
    jsonProperties.add("collectedDateTime");
    jsonProperties.add("collectedPeriod");
    jsonProperties.add("collectedQuantity");
    jsonProperties.add("collectionBodySiteCodableConcept");
    jsonProperties.add("collectionBodySiteReferenceId");
    jsonProperties.add("collectionComments");
    jsonProperties.add("collectionMethod");
    jsonProperties.add("collectorId");
    jsonProperties.add("containers");
    jsonProperties.add("@id");
    jsonProperties.add("parentIds");
    jsonProperties.add("receivedTime");
    jsonProperties.add("subject");
    jsonProperties.add("treatments");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.specimen, jsonProperties);
  }
}
