// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test code for Slot model.
 */
public class SlotTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSlot() throws IOException, URISyntaxException {
    Slot decoded = (Slot) geneticEncodeDecodeTest(TestModels.slot);

    assertTrue(decoded.getOverbooked());
    assertEquals(decoded.getType(), TestModels.codeable);
    assertEquals(decoded.getSchedule(), Id.of("Schedule"));
    assertEquals(decoded.getId(), Id.of("Slot"));
    assertEquals(decoded.getEnd(), TestModels.getDateTime());
    assertEquals(decoded.getStart(), TestModels.getDateTime());
    assertEquals(decoded.getFreeBusyType(), SlotFreeBusyType.BUSY);
    assertEquals(decoded.getComment(), "comment");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("comment");
    jsonProperties.add("end");
    jsonProperties.add("freeBusyType");
    jsonProperties.add("@id");
    jsonProperties.add("overbooked");
    jsonProperties.add("schedule");
    jsonProperties.add("start");
    jsonProperties.add("type");

    geneticJsonContainsFieldsTest(TestModels.slot, jsonProperties);
  }
}
