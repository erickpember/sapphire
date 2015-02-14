// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;

/**
 * Serializer for new time in Java
 */
@Slf4j
public class InstantDeserializer extends JsonDeserializer<Instant> {
  @Override
  public Instant deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    return Instant.parse(jp.getText());
  }
}
