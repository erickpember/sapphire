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
 * Test code for the Device Element in the Procedure model.
 */
public class ProcedureDeviceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testProcedureDevice() throws IOException, URISyntaxException {
    ProcedureDevice decoded = (ProcedureDevice) geneticEncodeDecodeTest(TestModels.procedureDevice);

    assertEquals(decoded.getAction(), TestModels.codeable);
    assertEquals(decoded.getManipulatedId(), Id.of("Device"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("action");
    jsonProperties.add("manipulatedId");

    geneticJsonContainsFieldsTest(TestModels.procedureDevice, jsonProperties);
  }
}
