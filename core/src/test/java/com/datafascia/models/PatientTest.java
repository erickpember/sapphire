// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Id;
import com.neovisionaries.i18n.LanguageCode;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Patient model.
 */
@Slf4j
public class PatientTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPatient() throws IOException, URISyntaxException {
    Patient decoded = (Patient) geneticEncodeDecodeTest(TestModels.patient);
    assertEquals(decoded.isActive(), true);
    assertEquals(decoded.getAddress(), TestModels.address);
    assertEquals(decoded.getBirthDate(), TestModels.getDate());
    assertEquals(decoded.getPhoto(), TestModels.getPhoto());
    assertEquals(decoded.getOrganization(), "Test Corp.");
    assertEquals(decoded.getName(), TestModels.name);
    assertEquals(decoded.getCareProvider(),
        Arrays.asList(TestModels.caregiver, TestModels.caregiver));
    assertEquals(decoded.getContactDetails(), Arrays.asList(TestModels.contact,
        TestModels.contact));
    assertEquals(decoded.getCreationDate(), TestModels.getDateTime());
    assertEquals(decoded.isDeceased(), false);
    assertEquals(decoded.getId(), Id.of("1234"));
    assertEquals(decoded.getLangs(), Arrays.asList(LanguageCode.en, LanguageCode.ch));
    assertEquals(decoded.getManagingOrg(), "Test Corp.");
    assertEquals(decoded.getMaritalStatus(), MaritalStatus.DOMESTIC_PARTNER);
    assertEquals(decoded.getRace(), Race.BLACK);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("@id");
    jsonProperties.add("contacts");
    jsonProperties.add("creationDate");
    jsonProperties.add("deceased");
    jsonProperties.add("maritalStatus");
    jsonProperties.add("race");
    jsonProperties.add("languages");
    jsonProperties.add("careProvider");
    jsonProperties.add("managingOrganization");
    jsonProperties.add("active");
    jsonProperties.add("institutionPatientId");
    geneticJsonContainsFieldsTest(TestModels.patient, jsonProperties);
  }
}
