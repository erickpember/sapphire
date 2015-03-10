// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import javax.measure.Unit;

/**
 * Serializes a UCUM unit.
 */
public class UnitSerializer extends JsonSerializer<Unit> {

  @Override
  public void serialize(Unit value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {
    jgen.writeString(UnitsSymbolMap.getName(value));
  }
}
