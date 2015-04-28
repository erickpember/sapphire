// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test code for Patient model.
 */
@Slf4j
public class PatientTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPatient() throws IOException, URISyntaxException {
    Patient decoded = (Patient) geneticEncodeDecodeTest(TestModels.patient);
    assertTrue(decoded.isActive());
    assertEquals(decoded.getGender(), Gender.FEMALE);
    assertEquals(decoded.getManagingOrganizationId(), Id.of("Organization"));
    assertEquals(decoded.getPhoto(), TestModels.getPhoto());
    assertEquals(decoded.getAddresses(), Arrays.asList(TestModels.address));
    assertEquals(decoded.getTelecoms(), Arrays.asList(TestModels.contactPoint));
    assertEquals(decoded.getNames(), Arrays.asList(TestModels.humanName));
    assertEquals(decoded.getLinks(), Arrays.asList(TestModels.personLink));
    assertEquals(decoded.getBirthDate(), TestModels.getDate());

    assertEquals(decoded.getMultipleBirthInteger(), new BigDecimal(9001));
    assertTrue(decoded.isDeceased());
    assertTrue(decoded.isMultipleBirthBoolean());
    assertEquals(decoded.getLastEncounterId(), Id.of("Encounter"));
    assertEquals(decoded.getId(), Id.of("Patient"));
    assertEquals(decoded.getCreationDate(), TestModels.getDateTime());
    assertEquals(decoded.getCareProviders(), Arrays.asList(TestModels.patientCareProvider));
    assertEquals(decoded.getCommunication(), TestModels.patientCommunication);
    assertEquals(decoded.getContacts(), Arrays.asList(TestModels.patientContact));
    assertEquals(decoded.getPatientLinks(), Arrays.asList(TestModels.patientLink));
    assertEquals(decoded.getMaritalStatus(), MaritalStatus.ANNULLED);
    assertEquals(decoded.getAnimal(), TestModels.patientAnimal);
    assertEquals(decoded.getRace(), Race.AMERICAN_INDIAN);
    assertEquals(decoded.getAccountNumber(), "accountNumber");
    assertEquals(decoded.getInstitutionPatientId(), "institutionPatientId");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("active");
    jsonProperties.add("addresses");
    jsonProperties.add("birthDate");
    jsonProperties.add("gender");
    jsonProperties.add("links");
    jsonProperties.add("managingOrganizationId");
    jsonProperties.add("names");
    jsonProperties.add("photo");
    jsonProperties.add("telecoms");

    jsonProperties.add("animal");
    jsonProperties.add("careProviders");
    jsonProperties.add("communication");
    jsonProperties.add("contacts");
    jsonProperties.add("creationDate");
    jsonProperties.add("deceased");
    jsonProperties.add("@id");
    jsonProperties.add("institutionPatientId");
    jsonProperties.add("lastEncounterId");
    jsonProperties.add("maritalStatus");
    jsonProperties.add("multipleBirthBoolean");
    jsonProperties.add("multipleBirthInteger");
    jsonProperties.add("patientLinks");
    jsonProperties.add("race");

    geneticJsonContainsFieldsTest(TestModels.patient, jsonProperties);
  }
}
