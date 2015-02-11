// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for range model.
 */
public class RangeTest extends ModelTestBase {
  @Test
  public <T extends Object> void testRange() throws IOException, URISyntaxException {
    Range decoded = (Range) geneticEncodeDecodeTest(TestModels.range);
    assertEquals(decoded.getLow(), TestModels.quantity);
    assertEquals(decoded.getHigh(), TestModels.quantity);
  }
}
