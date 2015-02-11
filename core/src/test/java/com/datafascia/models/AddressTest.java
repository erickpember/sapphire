// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.neovisionaries.i18n.CountryCode;
import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for address model.
 */
public class AddressTest extends ModelTestBase {
  @Test
  public <T extends Object> void testAddress() throws IOException, URISyntaxException {
    Address decoded = (Address) geneticEncodeDecodeTest(TestModels.address);
    assertEquals(decoded.getStreet(), "1234 Test Avenue");
    assertEquals(decoded.getCity(), "Test City");
    assertEquals(decoded.getStateProvince(), "Testlavania");
    assertEquals(decoded.getPostalCode(), "12345-6789");
    assertEquals(decoded.getUnit(), "F");
    assertEquals(decoded.getCountry(), CountryCode.US);
  }
}
