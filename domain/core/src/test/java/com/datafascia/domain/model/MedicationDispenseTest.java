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
 * Test code for the MedicationDispense model.
 */
public class MedicationDispenseTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationDispense() throws IOException, URISyntaxException {
    MedicationDispense decoded = (MedicationDispense) geneticEncodeDecodeTest(
        TestModels.medicationDispense);

    assertEquals(decoded.getSubstitutionType(), TestModels.codeable);
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getDestinationId(), Id.of("Destination"));
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getMedicationId(), Id.of("Medication"));
    assertEquals(decoded.getPatientId(), Id.of("Patient"));
    assertEquals(decoded.getDispenserId(), Id.of("Dispenser"));
    assertEquals(decoded.getWhenHandedOver(), TestModels.getDateTime());
    assertEquals(decoded.getWhenPrepared(), TestModels.getDateTime());
    assertEquals(decoded.getSubstitutionReasons(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getAuthorizingPrescriptionIds(), Arrays.asList(Id.
        of("AuthorizingPrescriptions")));
    assertEquals(decoded.getSubstitutionResponsiblePartyIds(), Arrays.asList(Id.of(
        "SubstitutionResponsibleParties")));
    assertEquals(decoded.getReceivers(), Arrays.asList(TestModels.medicationDispenseReceiver));
    assertEquals(decoded.getDosageInstructions(), TestModels.medicationDispenseDosageInstruction);
    assertEquals(decoded.getStatus(), MedicationDispenseStatus.ENTERED_IN_ERROR);
    assertEquals(decoded.getDaysSupply(), TestModels.numericQuantity);
    assertEquals(decoded.getQuantity(), TestModels.numericQuantity);
    assertEquals(decoded.getNote(), "note");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("authorizingPrescriptionIds");
    jsonProperties.add("daysSupply");
    jsonProperties.add("destinationId");
    jsonProperties.add("dispenserId");
    jsonProperties.add("dosageInstructions");
    jsonProperties.add("@id");
    jsonProperties.add("medicationId");
    jsonProperties.add("note");
    jsonProperties.add("patientId");
    jsonProperties.add("quantity");
    jsonProperties.add("receivers");
    jsonProperties.add("status");
    jsonProperties.add("substitutionReasons");
    jsonProperties.add("substitutionResponsiblePartyIds");
    jsonProperties.add("substitutionType");
    jsonProperties.add("type");
    jsonProperties.add("whenHandedOver");
    jsonProperties.add("whenPrepared");

    geneticJsonContainsFieldsTest(TestModels.medicationDispense, jsonProperties);
  }
}
