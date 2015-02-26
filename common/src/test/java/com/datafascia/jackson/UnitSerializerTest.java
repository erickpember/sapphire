// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.measure.Unit;
import org.testng.annotations.Test;
import tec.units.ri.util.UCUM;

import static org.testng.Assert.assertEquals;

/**
 * {@link UnitSerializer} test
 */
public class UnitSerializerTest extends UnitBaseTest {
  @Test
  public void should_serialize_unit() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    Unit unit = UCUM.METER;
    String name = UnitsSymbolMap.getName(unit);

    String json = objectMapper.writeValueAsString(name);
    assertEquals(json, "\"METER\"");
  }
}
