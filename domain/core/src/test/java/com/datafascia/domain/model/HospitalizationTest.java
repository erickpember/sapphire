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
 * Test code for hospitalization model.
 */
public class HospitalizationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testHospitalization() throws IOException, URISyntaxException {
    Hospitalization decoded = (Hospitalization) geneticEncodeDecodeTest(TestModels.hospitalization);

    assertEquals(decoded.getId(), Id.of("1234"));
    assertEquals(decoded.getOrigin(), TestModels.getURI());
    assertEquals(decoded.getPeriod(), TestModels.period);
    assertEquals(decoded.getAccomodation(), TestModels.accomodation);
    assertEquals(decoded.getDiet(), TestModels.codeable);
    assertEquals(decoded.getSpecialCourtesy(), TestModels.codeable);
    assertEquals(decoded.getSpecialArrangement(), TestModels.codeable);
    assertEquals(decoded.getDestination(), TestModels.getURI());
    assertEquals(decoded.getDischargeDisposition(), TestModels.codeable);
    assertEquals(decoded.getDischargeDiagnosis(), TestModels.getURI());
    assertEquals(decoded.isReadmission(), true);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("@id");
    jsonProperties.add("origin");
    jsonProperties.add("admitSource");
    jsonProperties.add("period");
    jsonProperties.add("accomodation");
    jsonProperties.add("diet");
    jsonProperties.add("specialCourtesy");
    jsonProperties.add("specialArrangement");
    jsonProperties.add("destination");
    jsonProperties.add("dischargeDisposition");
    jsonProperties.add("dischargeDiagnosis");
    jsonProperties.add("readmission");
    geneticJsonContainsFieldsTest(TestModels.hospitalization, jsonProperties);
  }
}
