// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import java.time.Instant;
import javax.measure.Unit;

/**
 * Module containing serializer/deserializers for units of measurement.
 */
public class UnitsMapperModule extends SimpleModule {
  /**
   * Create a new UnitsMapperModule
   */
  public UnitsMapperModule() {
    super();
    addSerializer(Instant.class, new InstantSerializer());
    addDeserializer(Instant.class, new InstantDeserializer());
    addSerializer(Unit.class, new UnitSerializer());
    addDeserializer(Unit.class, new UnitDeserializer());
  }
}
