// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for VaccinationProtocol Element in the Immunization model.
 */
public class ImmunizationVaccinationProtocolTest extends ModelTestBase {
  @Test
  public <T extends Object> void testImmunizationVaccinationProtocol() throws IOException,
      URISyntaxException {
    ImmunizationVaccinationProtocol decoded
        = (ImmunizationVaccinationProtocol) geneticEncodeDecodeTest(
            TestModels.immunizationVaccinationProtocol);

    assertEquals(decoded.getDoseSequence(), new BigDecimal(9001));
    assertEquals(decoded.getSeriesDoses(), new BigDecimal(9002));
    assertEquals(decoded.getDoseStatus(), TestModels.codeable);
    assertEquals(decoded.getDoseStatusReason(), TestModels.codeable);
    assertEquals(decoded.getDoseTarget(), TestModels.codeable);
    assertEquals(decoded.getAuthorityId(), Id.of("Organization"));
    assertEquals(decoded.getDescription(), "description");
    assertEquals(decoded.getSeries(), "series");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("authorityId");
    jsonProperties.add("description");
    jsonProperties.add("doseSequence");
    jsonProperties.add("doseStatus");
    jsonProperties.add("doseStatusReason");
    jsonProperties.add("doseTarget");
    jsonProperties.add("series");
    jsonProperties.add("seriesDoses");

    geneticJsonContainsFieldsTest(TestModels.immunizationVaccinationProtocol, jsonProperties);
  }
}
