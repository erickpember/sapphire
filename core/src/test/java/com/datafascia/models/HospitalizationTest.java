// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

/**
 * Test code for hospitalization model.
 */
public class HospitalizationTest extends ModelTestBase {
  @Test
  public <T extends Object> void testHospitalization() throws IOException, URISyntaxException {
    geneticEncodeDecodeTest(TestModels.hospitalization);
  }
}