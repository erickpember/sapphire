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
 * Test code for the Link Element in the Patient model.
 */
public class PatientLinkTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPatientLink() throws IOException, URISyntaxException {
    PatientLink decoded = (PatientLink) geneticEncodeDecodeTest(TestModels.patientLink);

    assertEquals(decoded.getOtherId(), Id.of("Patient"));
    assertEquals(decoded.getType(), PatientLinkType.REFER);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("otherId");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.patientLink, jsonProperties);
  }
}
