// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
