// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link com.datafascia.common.jackson.DurationDeserializer} test
 */
public class DurationDeserializerTest extends DurationBaseTest {
  @Test
  public void should_deserialize_duration() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    HealthcareServiceAvailableTime expectedTime = new HealthcareServiceAvailableTime();
    expectedTime.setAvailableStartTime(Duration.of(2, ChronoUnit.HOURS));
    String json = objectMapper.writeValueAsString(expectedTime);

    HealthcareServiceAvailableTime testTime = objectMapper.readValue(json,
        HealthcareServiceAvailableTime.class);
    assertEquals(testTime, expectedTime);
  }
}
