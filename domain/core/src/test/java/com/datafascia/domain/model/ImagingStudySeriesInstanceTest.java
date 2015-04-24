// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for the Instance Element in the Series Element in the InstanceImagingStudy model.
 */
public class ImagingStudySeriesInstanceTest extends ModelTestBase {
  @Test
  public <T extends Object> void testImagingStudySeriesInstance()
      throws IOException, URISyntaxException {
    ImagingStudySeriesInstance decoded
        = (ImagingStudySeriesInstance) geneticEncodeDecodeTest(
            TestModels.imagingStudySeriesInstance);

    assertEquals(decoded.getNumber(), new BigDecimal(9001));
    assertEquals(decoded.getContent(), Arrays.asList(TestModels.attachment));
    assertEquals(decoded.getSopClassOid(), Id.of("2.3.4.5"));
    assertEquals(decoded.getUid(), Id.of("2.3.4.5"));
    assertEquals(decoded.getTitle(), "title");
    assertEquals(decoded.getType(), "type");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("content");
    jsonProperties.add("number");
    jsonProperties.add("sopClassOid");
    jsonProperties.add("title");
    jsonProperties.add("type");
    jsonProperties.add("uid");

    geneticJsonContainsFieldsTest(TestModels.imagingStudySeriesInstance, jsonProperties);
  }
}
