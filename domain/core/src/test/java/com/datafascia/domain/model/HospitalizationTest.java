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
 * Test code for the Hospitalization model.
 */
public class HospitalizationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testHospitalization() throws IOException, URISyntaxException {
    Hospitalization decoded = (Hospitalization) geneticEncodeDecodeTest(TestModels.hospitalization);

    assertEquals(decoded.getAdmitSource(), TestModels.codeable);
    assertEquals(decoded.getDietPreference(), TestModels.codeable);
    assertEquals(decoded.getDischargeDisposition(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("Hospitalization"));
    assertEquals(decoded.getDestinationId(), Id.of("Location"));
    assertEquals(decoded.getOriginId(), Id.of("Location"));
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getSpecialArrangements(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getSpecialCourtesies(), Arrays.asList(TestModels.codeable));
    assertEquals(decoded.getDischargeDiagnosis(), TestModels.reference);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("admitSource");
    jsonProperties.add("destinationId");
    jsonProperties.add("dietPreference");
    jsonProperties.add("dischargeDiagnosis");
    jsonProperties.add("dischargeDisposition");
    jsonProperties.add("@id");
    jsonProperties.add("originId");
    jsonProperties.add("period");
    jsonProperties.add("specialArrangements");
    jsonProperties.add("specialCourtesies");

    geneticJsonContainsFieldsTest(TestModels.hospitalization, jsonProperties);
  }
}
