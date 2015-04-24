// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for MedicationPackage model.
 */
public class MedicationPackageTest extends ModelTestBase {
  @Test
  public <T extends Object> void testMedicationPackage() throws IOException, URISyntaxException {
    MedicationPackage decoded
        = (MedicationPackage) geneticEncodeDecodeTest(
            TestModels.medicationPackage);

    assertEquals(decoded.getContainer(), TestModels.codeable);
    assertEquals(decoded.getContents(), Arrays.asList(TestModels.content));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("container");
    jsonProperties.add("contents");

    geneticJsonContainsFieldsTest(TestModels.medicationPackage, jsonProperties);
  }
}
