// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

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
    ObjectMapper objectMapper = new ObjectMapper();
    Observation observ = new Observation();
    observ.setId(Id.of("1"));
    String json = objectMapper.writeValueAsString(observ);

    Observation observ1 = objectMapper.readValue(json, Observation.class);
    assertEquals(observ1, observ);
  }
}
