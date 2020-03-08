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
package com.datafascia.common.time;

import com.datafascia.common.jackson.DFObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test for Interval.
 */
@Slf4j
public class IntervalTest {

  @Test
  public void encodeDecodeTest() throws Exception {
    ObjectMapper mapper = DFObjectMapper.objectMapper();
    Interval<Instant> interval = new Interval<>(Instant.now(), Instant.now());
    String jsonString = mapper.writeValueAsString(interval);
    Object decoded = mapper.readValue(jsonString, new TypeReference<Interval<Instant>>() { });
    assertEquals(interval, decoded);
  }

  @Test
  public void beforeDuringAfter() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Date before = sdf.parse("2015-01-01 00:00:00");
    Date start = sdf.parse("2015-01-02 00:00:00");
    Date middle = sdf.parse("2015-01-02 01:00:00");
    Date end = sdf.parse("2015-02-01 00:00:00");
    Date after = sdf.parse("2015-02-01 00:00:01");

    Interval<Date> interval = new Interval<Date>(start, end);
    // Start inclusive
    assertTrue(interval.contains(start));

    // In between value
    assertTrue(interval.contains(middle));

    // End exclusive
    assertFalse(interval.contains(end));

    // Exclude values before
    assertFalse(interval.contains(before));

    // Exclude values after
    assertFalse(interval.contains(after));
  }
}
