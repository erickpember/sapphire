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
 * Test code for the BodySite Element in the Procedure model.
 */
public class ProcedureBodySiteTest extends ModelTestBase {
  @Test
  public <T extends Object> void testProcedureBodySite() throws IOException, URISyntaxException {
    ProcedureBodySite decoded = (ProcedureBodySite) geneticEncodeDecodeTest(
        TestModels.procedureBodySite);

    assertEquals(decoded.getSiteCodeableConcept(), TestModels.codeable);
    assertEquals(decoded.getSiteReferenceId(), Id.of("BodySite"));
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("siteCodeableConcept");
    jsonProperties.add("siteReferenceId");

    geneticJsonContainsFieldsTest(TestModels.procedureBodySite, jsonProperties);
  }
}
