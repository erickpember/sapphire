// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link IdSerializer} test
 */
public class IdSerializerTest extends IdBaseTest {
  @Test
  public void should_serialize_id() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Observation observ = new Observation();
    observ.setId(Id.of("1"));

    String json = objectMapper.writeValueAsString(observ);
    assertEquals(json, "{\"@id\":\"urn:test-IdBaseTest:1\"}");
  }
}
