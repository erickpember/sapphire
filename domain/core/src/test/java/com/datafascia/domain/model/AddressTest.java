// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.neovisionaries.i18n.CountryCode;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Address model.
 */
public class AddressTest extends ModelTestBase {
  @Test
  public <T extends Object> void testAddress() throws IOException, URISyntaxException {
    Address decoded = (Address) geneticEncodeDecodeTest(TestModels.address);
    assertEquals(decoded.getLines(), Arrays.asList("1234 Test Avenue"));
    assertEquals(decoded.getText(), "text");
    assertEquals(decoded.getCity(), "Test City");
    assertEquals(decoded.getStateProvince(), "Testlavania");
    assertEquals(decoded.getPostalCode(), "12345-6789");
    assertEquals(decoded.getUnit(), "F");
    assertEquals(decoded.getCountry(), CountryCode.US);
    assertEquals(decoded.getUse(), AddressUse.HOME);
    assertEquals(decoded.getPeriod(), TestModels.period);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("lines");
    jsonProperties.add("city");
    jsonProperties.add("stateProvince");
    jsonProperties.add("postalCode");
    jsonProperties.add("unit");
    jsonProperties.add("country");
    jsonProperties.add("use");
    geneticJsonContainsFieldsTest(TestModels.address, jsonProperties);
  }
}
