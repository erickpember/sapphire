// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test for HumanName model
 */
public class HumanNameTest extends ModelTestBase {
  HumanName nullName = new HumanName() {
    {
      setGiven(null);
      setFamily(null);
    }
  };

  HumanName longName = new HumanName() {
    {
      setGiven(Arrays.asList("John Jacob Johnny JoeBob JimJoe JB Jezekiah Jammyjamjingle Hallston "
          + "Thurston Dunston Winston Fallstone Firestone Firestein Feuerstein Rammstein Bierstein "
          + "Dig-A-Dig-Dag Whopbopaloobopawapabamboom"));
      setFamily(Arrays.asList("Loblolly Linden Zeddemore Gambolputty de von Ausfern "
          + "Sphlendenschlitt Digger Dagger Dongle Tikolensic Grander Knotty Spelltinkle "
          + "Grumblemeyer Luber Hundsfut Von Hauptkopf of Ulm"));
    }
  };

  HumanName arabicName = new HumanName() {
    {
      setGiven(Arrays.asList("سعد"));
      setFamily(Arrays.asList("الهمام"));
    }
  };

  HumanName firstMiddleLastName = new HumanName() {
    {
      setGiven(Arrays.asList("Gráinne", "Ní"));
      setFamily(Arrays.asList("Mháille"));
    }
  };

  HumanName firstLastName = new HumanName() {
    {
      setGiven(Arrays.asList("Gráinne"));
      setFamily(Arrays.asList("Mháille"));
    }
  };

  @Test
  public <T extends Object> void testName() throws IOException, URISyntaxException {
    HumanName decoded = (HumanName) geneticEncodeDecodeTest(TestModels.humanName);
    assertEquals(decoded.getGiven(), Arrays.asList("Tester"));
    assertEquals(decoded.getFamily(), Arrays.asList("Testington"));
  }

  @Test
  public void formatFirstMiddleLast() {
    assertEquals(firstMiddleLastName.format("<given:{givenPart | <givenPart> }>"
        + HumanName.FAMILY_FM), "Gráinne Ní Mháille");
  }

  @Test
  public void nullTest() {
    assertEquals(nullName.format("<[" + HumanName.GIVEN + ","
        + HumanName.FAMILY
        + "]; separator=\" \">"), "");
  }

  @Test
  public void arabicTest() {
    assertEquals(arabicName.format("<[" + HumanName.GIVEN + ","
        + HumanName.FAMILY
        + "]; separator=\" \">"), "سعد الهمام");
  }

  @Test
  public void formatEmergeStyle() {
    assertEquals(
        firstMiddleLastName.format("<[" + HumanName.GIVEN + "," + HumanName.FAMILY
            + "]; separator=\" \">"), "Gráinne Ní Mháille");
  }

  @Test
  public void optionalMiddle() {
    assertEquals(firstLastName.format("<[" + HumanName.GIVEN + "," + HumanName.FAMILY
        + "]; separator=\" \">"), "Gráinne Mháille");
  }

  @Test
  public void longNameTest() {
    assertEquals(longName.format("<[" + HumanName.GIVEN + "," + HumanName.FAMILY
        + "]; separator=\" \">"), "John Jacob Johnny JoeBob JimJoe JB Jezekiah Jammyjamjingle "
        + "Hallston Thurston Dunston Winston Fallstone Firestone Firestein Feuerstein "
        + "Rammstein Bierstein Dig-A-Dig-Dag Whopbopaloobopawapabamboom Loblolly Linden "
        + "Zeddemore Gambolputty de von Ausfern Sphlendenschlitt Digger Dagger "
        + "Dongle Tikolensic Grander Knotty Spelltinkle Grumblemeyer Luber Hundsfut Von "
        + "Hauptkopf of Ulm");
  }

  @Test
  public void setFirstNameTest() {
    HumanName name = new HumanName();
    name.setFirstName("bob");
    assertEquals(name.getFirstName(), "bob");
    assertEquals(name.getGiven().
        get(0), "bob");
    name.setFirstName("tom");
    assertEquals(name.getFirstName(), "tom");
    assertEquals(name.getGiven().
        size(), 1);
  }

  @Test
  public void setMiddleNameTest() {
    HumanName name = new HumanName();
    assertEquals(name.getMiddleName(), null);
    name.setMiddleName("the");
    assertTrue(name.getFirstName().
        isEmpty());
    assertEquals(name.getMiddleName(), "the");
    name.setFirstName("Winnie");
    assertEquals(name.getMiddleName(), "the");
    assertEquals(name.getFirstName(), "Winnie");
    assertEquals(name.getGiven().
        size(), 2);
  }

  @Test
  public void setLastNameTest() {
    HumanName name = new HumanName();
    name.setLastName("Gambolputty");
    assertEquals(name.getLastName(), "Gambolputty");
    assertEquals(name.getFamily().
        get(0), "Gambolputty");
    name.setLastName("of Ulm");
    assertEquals(name.getLastName(), "of Ulm");
    assertEquals(name.getFamily().
        size(), 2);
  }

  @Test
  public void testJsonProperties() throws IOException {
    ArrayList<String> jsonProperties = new ArrayList<>();
    jsonProperties.add(HumanName.GIVEN);
    jsonProperties.add(HumanName.FAMILY);
    geneticJsonContainsFieldsTest(TestModels.humanName, jsonProperties);
  }
}
