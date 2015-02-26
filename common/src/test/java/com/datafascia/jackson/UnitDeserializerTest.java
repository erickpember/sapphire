// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javax.measure.Unit;
import org.testng.annotations.Test;
import tec.units.ri.util.UCUM;

import static org.testng.Assert.assertEquals;

/**
 * {@link UnitDeserializer} test
 */
public class UnitDeserializerTest extends LocalDateBaseTest {
  @Test
  public void should_deserialize_unit() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new SimpleModule() {
      {
        addDeserializer(Unit.class, new UnitDeserializer());
      }
    });
    Unit unit = UCUM.METER;
    String name = UnitsSymbolMap.getName(unit);
    String json = objectMapper.writeValueAsString(name);

    Unit testUnit = objectMapper.readValue(json, Unit.class);
    assertEquals(testUnit, unit);
  }
}
