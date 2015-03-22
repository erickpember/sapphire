// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson deserializer for {@link Id}.
 * <p>
 * NOTE: The SuppressWarnings is necessary because we do not have the type parameter for the Id.
 */
@Slf4j @SuppressWarnings("rawtypes")
public class IdDeserializer extends StdDeserializer<Id> {

  /**
   * Constructor
   */
  public IdDeserializer() {
    super(Id.class);
  }

  @Override
  public Id deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
    return Id.of(jsonParser.getText());
  }
}
