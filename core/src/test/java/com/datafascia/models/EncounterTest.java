// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * Test for the Encounter model.
 */
@Slf4j
public class EncounterTest extends ModelTestBase {
  @Test
  public <T extends Object> void testEncounter() throws IOException, URISyntaxException {
    geneticEncodeDecodeTest(TestModels.encounter);
  }
}
