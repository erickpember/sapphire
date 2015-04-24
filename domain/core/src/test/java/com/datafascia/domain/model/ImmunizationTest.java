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
import static org.testng.Assert.assertTrue;

/**
 * Test code for Immunization model.
 */
public class ImmunizationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testImmunization() throws IOException, URISyntaxException {
    Immunization decoded = (Immunization) geneticEncodeDecodeTest(TestModels.immunization);

    assertTrue(decoded.getReported());
    assertTrue(decoded.getWasNotGiven());
    assertEquals(decoded.getRoute(), TestModels.codeable);
    assertEquals(decoded.getSite(), TestModels.codeable);
    assertEquals(decoded.getVaccineType(), TestModels.codeable);
    assertEquals(decoded.getEncounterId(), Id.of("Encounter"));
    assertEquals(decoded.getId(), Id.of("Immunization"));
    assertEquals(decoded.getLocationId(), Id.of("Location"));
    assertEquals(decoded.getManufacturerId(), Id.of("Organization"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPerformerId(), Id.of("Practitioner"));
    assertEquals(decoded.getRequesterId(), Id.of("Practitioner"));
    assertEquals(decoded.getDate(), TestModels.getDateTime());
    assertEquals(decoded.getReasonsGiven(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getReasonsNotGiven(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getReactions(), Arrays.asList(TestModels.immunizationReaction));
    assertEquals(decoded.getVaccinationProtocols(), Arrays.asList(
        TestModels.immunizationVaccinationProtocol));
    assertEquals(decoded.getExpirationDate(), TestModels.getDate());
    assertEquals(decoded.getDoseQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getLotNumber(), "lotNumber");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("date");
    jsonProperties.add("doseQuantity");
    jsonProperties.add("encounterId");
    jsonProperties.add("expirationDate");
    jsonProperties.add("@id");
    jsonProperties.add("locationId");
    jsonProperties.add("lotNumber");
    jsonProperties.add("manufacturerId");
    jsonProperties.add("patientId");
    jsonProperties.add("performerId");
    jsonProperties.add("reactions");
    jsonProperties.add("reasonsGiven");
    jsonProperties.add("reasonsNotGiven");
    jsonProperties.add("reported");
    jsonProperties.add("requesterId");
    jsonProperties.add("route");
    jsonProperties.add("site");
    jsonProperties.add("vaccinationProtocols");
    jsonProperties.add("vaccineType");
    jsonProperties.add("wasNotGiven");

    geneticJsonContainsFieldsTest(TestModels.immunization, jsonProperties);
  }
}
