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

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link IdDeserializer} test
 */
public class IdDeserializerTest extends IdBaseTest {

  @Test
  public void should_deserialize_id() throws Exception {
    ObjectMapper objectMapper = DFObjectMapper.objectMapper();

    Observation originalObservation = new Observation();
    originalObservation.setId(Id.of("urn:test-IdBaseTest:1"));
    String json = objectMapper.writeValueAsString(originalObservation);

    Observation observation = objectMapper.readValue(json, Observation.class);
    assertEquals(observation, originalObservation);
  }
}
