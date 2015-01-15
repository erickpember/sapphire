// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for name model
 */
public class NameTest {
  Name firstLast = new Name() {{
    setFirst("John");
    setLast("Doe");
  }};

  Name firstMiddleLast = new Name() {{
    setFirst("John");
    setMiddle("Middle");
    setLast("Doe");
  }};

  @Test
  public void formatFirstLast() {
    assertEquals(firstLast.format(Name.FIRST_FM + " " + Name.LAST_FM), "John Doe");
  }

  @Test
  public void formatFirstLastMiddle() {
    assertEquals(firstMiddleLast.format(Name.FIRST_FM + Name.LAST_FM + Name.MIDDLE_FM), "JohnDoeMiddle");
  }

  @Test
  public void optionalMiddle() {
    assertEquals(firstLast.format("<[" + Name.FIRST + "," + Name.MIDDLE + "," + Name.LAST +
          "]; separator=\" \">"), "John Doe");
  }
}
