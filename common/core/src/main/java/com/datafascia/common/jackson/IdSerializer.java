// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.datafascia.common.persist.Id;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Jackson serializer for {@link Id}.
 * <p>
 * NOTE: The SuppressWarnings is necessary because we do not have the type parameter for the Id.
 */
@Slf4j @SuppressWarnings("rawtypes")
public class IdSerializer extends StdSerializer<Id> {
  /**
   * Null constructor needed by Jackson. Should never be called otherwise
   */
  public IdSerializer() {
    super(Id.class);
  }

  @Override
  public void serialize(Id value, JsonGenerator jsonGenerator, SerializerProvider provider)
      throws IOException {

    jsonGenerator.writeString(value.toString());
  }
}
