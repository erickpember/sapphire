// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test code for Order model.
 */
public class OrderTest extends ModelTestBase {
  @Test
  public <T extends Object> void testOrder() throws IOException, URISyntaxException {
    Order decoded = (Order) geneticEncodeDecodeTest(TestModels.order);

    assertEquals(decoded.getReasonCodeableConcept(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("Order"));
    assertEquals(decoded.getSourceId(), Id.of("Source"));
    assertEquals(decoded.getDateTime(), TestModels.getDateTime());
    assertEquals(decoded.getDetails(), Arrays.asList(TestModels.reference));
    assertEquals(decoded.getSubject(), TestModels.orderSubject);
    assertEquals(decoded.getTarget(), TestModels.orderTarget);
    assertEquals(decoded.getWhen(), TestModels.orderWhen);
    assertEquals(decoded.getAuthority(), TestModels.reference);
    assertEquals(decoded.getReasonReference(), TestModels.reference);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("authority");
    jsonProperties.add("dateTime");
    jsonProperties.add("details");
    jsonProperties.add("@id");
    jsonProperties.add("sourceId");
    jsonProperties.add("reasonCodeableConcept");
    jsonProperties.add("reasonReference");
    jsonProperties.add("subject");
    jsonProperties.add("target");
    jsonProperties.add("when");

    geneticJsonContainsFieldsTest(TestModels.order, jsonProperties);
  }
}
