// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.measure.Unit;
import org.testng.annotations.Test;
import tec.units.ri.util.UCUM;

import static org.testng.Assert.assertEquals;

/**
 * {@link com.datafascia.common.jackson.UnitDeserializer} test
 */
public class UnitDeserializerTest extends LocalDateBaseTest {
  @Test
  public void should_deserialize_unit() throws Exception {
    ObjectMapper objectMapper = DFObjectMapper.objectMapper();
    Unit unit = UCUM.METER;
    String name = UnitsSymbolMap.getName(unit);
    String json = objectMapper.writeValueAsString(name);

    Unit testUnit = objectMapper.readValue(json, Unit.class);
    assertEquals(testUnit, unit);
  }
}
