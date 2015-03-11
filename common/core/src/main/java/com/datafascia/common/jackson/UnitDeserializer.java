// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import javax.measure.Unit;

/**
 * Deserializes a UCUM unit.
 */
public class UnitDeserializer extends JsonDeserializer<Unit> {
  @Override
  public Unit deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException {
    return UnitsSymbolMap.getUnit(jp.getText());
  }
}
