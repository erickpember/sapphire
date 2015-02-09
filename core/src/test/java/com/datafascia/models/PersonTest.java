// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

/**
 * Test code for person model.
 */
public class PersonTest extends ModelTestBase {
  @Test
  public <T extends Object> void testPerson() throws IOException, URISyntaxException {
    geneticEncodeDecodeTest(TestModels.person);
  }
}
