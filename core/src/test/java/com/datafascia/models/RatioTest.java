// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ratio model.
 */
public class RatioTest extends ModelTestBase {
  @Test
  public <T extends Object> void testRatio() throws IOException, URISyntaxException {
    Ratio decoded = (Ratio) geneticEncodeDecodeTest(TestModels.ratio);
    assertEquals(decoded.getNumerator(), TestModels.quantity);
    assertEquals(decoded.getDenominator(), TestModels.quantity);
  }
}
