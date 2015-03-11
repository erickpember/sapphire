// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact

package com.datafascia.common.jackson;

import java.util.HashMap;
import java.util.Map;
import javax.measure.Unit;
import tec.units.ri.util.SI;
import tec.units.ri.util.UCUM;

/**
 * Provides access to mappings of unit names to their respective objects.
 */
public class UnitsSymbolMap {

  private static final Map<String, Unit> unitsSymbolMap = new HashMap<String, Unit>() {
    {
      put("KILOGRAM", SI.KILOGRAM);
      put("METER", UCUM.METER);
      put("CELSIUS", UCUM.CELSIUS);
      put("KELVIN", UCUM.KELVIN);
      put("LITER", UCUM.LITER);
      put("INCH_INTERNATIONAL", UCUM.INCH_INTERNATIONAL);
      put("FOOT_INTERNATIONAL", UCUM.FOOT_INTERNATIONAL);
      put("GALLON_US", UCUM.GALLON_US);
      put("QUART_US", UCUM.QUART_US);
      put("PINT_US", UCUM.PINT_US);
      put("FLUID_OUNCE_US", UCUM.FLUID_OUNCE_US);
      put("FAHRENHEIT", UCUM.FAHRENHEIT);
      put("BTU", UCUM.BTU);
      put("POUND_PER_SQUARE_INCH", UCUM.POUND_PER_SQUARE_INCH);
      put("PERCENT", UCUM.PERCENT);
      put("GRAM", UCUM.GRAM);


    }
  };

  /*
  This has short versions of the names for looking up units. It need to be a separate map so the
  units and names have a 1:1 mapping in the primary map.
   */
  private static final Map<String, Unit> unitsShortSymbolMap = new HashMap<String, Unit>() {
    {
      put("kg", SI.KILOGRAM);
      put("in", UCUM.INCH_INTERNATIONAL);
      put("m", UCUM.METER);
    }
  };

  /**
   * Get a name for a given unit.
   *
   * @param unit The unit to get a name for.
   * @return The name for the given unit.
   */
  public static String getName(Unit unit) {
    for (Map.Entry<String, Unit> entry : unitsSymbolMap.entrySet()) {
      if (unit.equals(entry.getValue())) {
        return entry.getKey();
      }
    }
    throw new UnsupportedUnitException("Unknown unit type provided.");
  }

  /**
   * Get a unit for a given name.
   *
   * @param name The name for a given unit.
   * @return The unit for a given name.
   */
  public static Unit getUnit(String name) {
    Unit unit = unitsSymbolMap.get(name);
    if (unit == null) {
      unit = unitsShortSymbolMap.get(name);
      if (unit == null) {
        throw new UnsupportedUnitException("Unknown unit name provided: " + name);
      }
    }
    return unit;
  }

}
