// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Id;
import com.neovisionaries.i18n.LanguageCode;
import java.io.IOException;
import java.net.URISyntaxException;
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
    assertEquals(decoded.getPhoto(), TestModels.getURI());
    assertEquals(decoded.getOrganization(), "Test Corp.");
    assertEquals(decoded.getName(), TestModels.name);
    assertEquals(decoded.getCareProvider(),
        Arrays.asList(TestModels.caregiver, TestModels.caregiver));
    assertEquals(decoded.getContactDetails(), Arrays.asList(TestModels.contact,
        TestModels.contact));
    assertEquals(decoded.getCreationDate(), TestModels.getDate());
    assertEquals(decoded.isDeceased(), false);
    assertEquals(decoded.getId(), Id.of("1234"));
    assertEquals(decoded.getLangs(), Arrays.asList(LanguageCode.en, LanguageCode.ch));
    assertEquals(decoded.getManagingOrg(), "Test Corp.");
    assertEquals(decoded.getMaritalStatus(), MaritalStatus.DomesticPartner);
    assertEquals(decoded.getRace(), Race.Black);
  }
}
