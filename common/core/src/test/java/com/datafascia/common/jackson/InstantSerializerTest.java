// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.datafascia.common.jackson.InstantSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link com.datafascia.common.jackson.InstantSerializer} test
 */
public class InstantSerializerTest {

  @AllArgsConstructor @Data
  private static class Record {
    @JsonSerialize(using = InstantSerializer.class)
    private Instant instant;
  }

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  public void should_format_milliseconds() throws Exception {
    Record record = new Record(Instant.parse("2015-01-23T12:34:56Z"));
    String json = OBJECT_MAPPER.writeValueAsString(record);
    assertEquals(json, "{\"instant\":\"2015-01-23T12:34:56.000Z\"}");
  }
}
