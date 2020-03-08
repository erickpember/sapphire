// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
