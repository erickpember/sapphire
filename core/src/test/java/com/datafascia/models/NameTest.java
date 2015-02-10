// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import java.io.IOException;
import java.net.URISyntaxException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for name model
 */
public class NameTest extends ModelTestBase {
  Name firstLast = new Name() {
    {
      setFirst("John");
      setLast("Doe");
    }
  };

  Name firstMiddleLast = new Name() {
    {
      setFirst("John");
      setMiddle("Middle");
      setLast("Doe");
    }
  };

  Name nullName = new Name() {
    {
      setFirst(null);
      setMiddle(null);
      setLast(null);
    }
  };

  Name longName = new Name() {
    {
      setFirst("John Jacob Johnny JoeBob JimJoe JB Jezekiah Jammyjamjingle");
      setMiddle("Hallston Thurston Dunston Winston Fallstone Firestone Firestein Feuerstein "
          + "Rammstein Bierstein Dig-A-Dig-Dag Whopbopaloobopawapabamboom");
      setLast("Loblolly Linden Zeddemore Gambolputty de von Ausfern Sphlendenschlitt Digger Dagger"
          + "Dongle Tikolensic Grander Knotty Spelltinkle Grumblemeyer Luber Hundsfut Von "
          + "Hauptkopf of Ulm");
    }
  };

  Name arabicName = new Name() {
    {
      setFirst("سعد");
      setLast("الهمام");
    }
  };

  @Test
  public <T extends Object> void testName() throws IOException, URISyntaxException {
    geneticEncodeDecodeTest(TestModels.name);
  }

  @Test
  public void formatFirstLast() {
    assertEquals(firstLast.format(Name.FIRST_FM + " " + Name.LAST_FM), "John Doe");
  }

  @Test
  public void formatFirstLastMiddle() {
    assertEquals(
        firstMiddleLast.format(Name.FIRST_FM + Name.LAST_FM + Name.MIDDLE_FM), "JohnDoeMiddle");
  }

  @Test
  public void optionalMiddle() {
    assertEquals(firstLast.format("<[" + Name.FIRST + "," + Name.MIDDLE + "," + Name.LAST
        + "]; separator=\" \">"), "John Doe");
  }

  @Test
  public void nullTest() {
    assertEquals(nullName.format("<[" + Name.FIRST + "," + Name.MIDDLE + "," + Name.LAST
        + "]; separator=\" \">"), "");
  }

  @Test
  public void arabicTest() {
    assertEquals(arabicName.format("<[" + Name.FIRST + "," + Name.MIDDLE + "," + Name.LAST
        + "]; separator=\" \">"), "سعد الهمام");
  }

  @Test
  public void longNameTest() {
    assertEquals(longName.format("<[" + Name.FIRST + "," + Name.MIDDLE + "," + Name.LAST
        + "]; separator=\" \">"), "John Jacob Johnny JoeBob JimJoe JB Jezekiah Jammyjamjingle "
        + "Hallston Thurston Dunston Winston Fallstone Firestone Firestein Feuerstein "
        + "Rammstein Bierstein Dig-A-Dig-Dag Whopbopaloobopawapabamboom Loblolly Linden "
        + "Zeddemore Gambolputty de von Ausfern Sphlendenschlitt Digger Dagger"
        + "Dongle Tikolensic Grander Knotty Spelltinkle Grumblemeyer Luber Hundsfut Von "
        + "Hauptkopf of Ulm");
  }
}
