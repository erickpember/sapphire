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
 * Test code for the InformationSource Element in the MedicationStatement model.
 */
public class MedicationStatementInformationSourceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationStatementInformationSource() throws IOException,
      URISyntaxException {
    MedicationStatementInformationSource decoded
        = (MedicationStatementInformationSource) geneticEncodeDecodeTest(
            TestModels.medicationStatementInformationSource);

    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getPractitionerId(), Id.of("Practitioner"));
    assertEquals(decoded.getRelatedPersonId(), Id.of("RelatedPerson"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("patientId");
    jsonProperties.add("practitionerId");
    jsonProperties.add("relatedPersonId");

    geneticJsonContainsFieldsTest(TestModels.medicationStatementInformationSource, jsonProperties);
  }
}
