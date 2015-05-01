// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Test code for Medication model.
 */
public class MedicationTest extends ModelTestBase {

  @Test
  public void testMedication() throws Exception {
    Medication decoded = (Medication) geneticEncodeDecodeTest(TestModels.medication);

    assertFalse(decoded.getIsBrand());
    assertEquals(decoded.getCode(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("id"));
    assertEquals(decoded.getPackage(), TestModels.medicationPackage);
    assertEquals(decoded.getManufacturerId(), Id.of("manufacturer"));
    assertEquals(decoded.getProduct(), TestModels.product);
    assertEquals(decoded.getKind(), "kind");
    assertEquals(decoded.getName(), "name");
  }

  @Test
  public void testJsonProperties() throws Exception {
    List<String> jsonProperties = Arrays.asList(
        "@id",
        "code",
        "isBrand",
        "kind",
        "manufacturerId",
        "name",
        "package",
        "product");

    geneticJsonContainsFieldsTest(TestModels.medication, jsonProperties);
  }
}
