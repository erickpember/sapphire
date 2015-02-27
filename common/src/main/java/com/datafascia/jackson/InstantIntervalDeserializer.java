// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact

package com.datafascia.jackson;

import com.datafascia.common.time.Interval;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;

/**
 * Deserializes an Interval containing instants.
 */
public class InstantIntervalDeserializer extends JsonDeserializer<Interval<Instant>> {
  static final TypeReference<Interval<Instant>> intType =
      new TypeReference<Interval<Instant>>() {
      };

  @Override
  public Interval<Instant> deserialize(JsonParser jsonParser, DeserializationContext
      deserializationContext) throws IOException {
    return jsonParser.readValueAs(intType);
  }
}
