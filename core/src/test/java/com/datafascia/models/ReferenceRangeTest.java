// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for ReferenceRange model.
 */
public class ReferenceRangeTest extends ModelTestBase {
  @Test
  public <T extends Object> void testReferenceRange() throws IOException, URISyntaxException {
    ReferenceRange decoded = (ReferenceRange) geneticEncodeDecodeTest(TestModels.referenceRange);
    assertEquals(decoded.getMeaning(), TestModels.codeable);
    assertEquals(decoded.getAge(), TestModels.range);
  }
}
