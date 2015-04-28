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
 * Test code for Schedule model.
 */
public class ScheduleTest extends ModelTestBase {
  @Test
  public <T extends Object> void testSchedule() throws IOException, URISyntaxException {
    Schedule decoded = (Schedule) geneticEncodeDecodeTest(TestModels.schedule);

    assertEquals(decoded.getTypes(), TestModels.codeable);
    assertEquals(decoded.getId(), Id.of("Schedule"));
    assertEquals(decoded.getPlanningHorizon(), TestModels.period);
    assertEquals(decoded.getActor(), TestModels.reference);
    assertEquals(decoded.getComment(), "comment");
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add("actor");
    jsonProperties.add("comment");
    jsonProperties.add("@id");
    jsonProperties.add("planningHorizon");
    jsonProperties.add("types");

    geneticJsonContainsFieldsTest(TestModels.schedule, jsonProperties);
  }
}
