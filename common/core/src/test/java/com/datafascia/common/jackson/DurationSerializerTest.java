// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link com.datafascia.common.jackson.DurationSerializer} test
 */
public class DurationSerializerTest extends DurationBaseTest {
  @Test
  public void should_serialize_duration() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    HealthcareServiceAvailableTime time = new HealthcareServiceAvailableTime();
    time.setAvailableStartTime(Duration.of(2, ChronoUnit.HOURS));

    String json = objectMapper.writeValueAsString(time);
    assertEquals(json, "{\"availableStartTime\":\"PT2H\"}");
  }
}
