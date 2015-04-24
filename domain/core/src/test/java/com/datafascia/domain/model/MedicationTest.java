// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Test code for Medication model.
 */
public class MedicationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedication() throws IOException, URISyntaxException {
    Medication decoded = (Medication) geneticEncodeDecodeTest(TestModels.medication);

    assertFalse(decoded.getBrand());
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getMedicationPackage(), TestModels.medicationPackage);
    assertEquals(decoded.getManufacturerId(), Id.of("manufacturer"));
    assertEquals(decoded.getProduct(), TestModels.product);
    assertEquals(decoded.getKind(), "kind");
    assertEquals(decoded.getName(), "name");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("brand");
    jsonProperties.add("code");
    jsonProperties.add("@id");
    jsonProperties.add("kind");
    jsonProperties.add("manufacturerId");
    jsonProperties.add("medicationPackage");
    jsonProperties.add("name");
    jsonProperties.add("product");

    geneticJsonContainsFieldsTest(TestModels.medication, jsonProperties);
  }
}
