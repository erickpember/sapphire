// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.domain.model.IngestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for raw message
 */
@Slf4j
public class IngestMessageTest {
  IngestMessage message = new IngestMessage() {
    {
      try {
        setTimestamp(Instant.ofEpochSecond(10234567));
        setInstitution(new URI("urn:df-institution-1:ucsf"));
        setFacility(new URI("urn:df-facility-1:parnassus"));
        setDepartment(new URI("urn:df-icu-1:icu"));
        setSource(new URI("urn:df-source-1:bed1"));
        setPayloadType(new URI("urn:df-payload-1:hl7"));
        setPayload("XXXXXXXXXXXXXXXXXXXXX");
      } catch (URISyntaxException ex) {
        throw new RuntimeException("Internal error generating URI. Not expected.");
      }
    }
  };

  @Test
  public void encodeTest() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(message);
    assertEquals(json,
        "{\"@type\":\"urn:df-model-1:IngestMessage\",\"timestamp\":\"1970-04-29T10:56:07.000Z\"" +
        ",\"institution\":\"urn:df-institution-1:ucsf\"," +
        "\"facility\":\"urn:df-facility-1:parnassus\",\"department\":\"urn:df-icu-1:icu\"," +
        "\"source\":\"urn:df-source-1:bed1\",\"payloadType\":\"urn:df-payload-1:hl7\"" +
        ",\"payload\":\"XXXXXXXXXXXXXXXXXXXXX\"}");
  }

  @Test
  public void decodeTest() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(message);
    IngestMessage message1 = objectMapper.readValue(json, IngestMessage.class);
    assertEquals(message1, message);
  }
}
