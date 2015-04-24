// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;

/**
 * Deserializer for Java {@link Duration}
 */
@Slf4j
public class DurationDeserializer extends JsonDeserializer<Duration> {
  @Override
  public Duration deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    return Duration.parse(jp.getText());
  }
}
