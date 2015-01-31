// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.json;

import static org.testng.Assert.assertEquals;

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Data;
import org.testng.annotations.Test;

/**
 * {@link IdSerializer} test
 */
public class IdSerializerTest {

  @Data
  private static class Patient {
    private Id<Patient> id;
  }

  @Test
  public void should_serialize_id() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(
        new SimpleModule()
            .addSerializer(new IdSerializer()));

    Patient patient = new Patient();
    patient.setId(Id.of("1"));

    String json = objectMapper.writeValueAsString(patient);
    assertEquals(json, "{\"id\":\"Patient:1\"}");
  }
}
