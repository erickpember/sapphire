// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.primitive.StringDt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for HumanNames utility
 */
public class HumanNamesTest {
  HumanNameDt nullName = new HumanNameDt() {
    {
      setGiven(null);
      setFamily(null);
    }
  };

  HumanNameDt longName = new HumanNameDt() {
    {
      setGiven(convertToStringDt(Arrays.asList("John Jacob Johnny JoeBob JimJoe JB Jezekiah "
          + "Jammyjamjingle Hallston Thurston Dunston Winston Fallstone Firestone Firestein "
          + "Feuerstein Rammstein Bierstein Dig-A-Dig-Dag Whopbopaloobopawapabamboom")));
      setFamily(convertToStringDt(Arrays.asList(
          "Loblolly Linden Zeddemore Gambolputty de von Ausfern "
          + "Sphlendenschlitt Digger Dagger Dongle Tikolensic Grander Knotty Spelltinkle "
          + "Grumblemeyer Luber Hundsfut Von Hauptkopf of Ulm")));
    }
  };

  HumanNameDt arabicName = new HumanNameDt() {
    {
      setGiven(convertToStringDt(Arrays.asList("سعد")));
      setFamily(convertToStringDt(Arrays.asList("الهمام")));
    }
  };

  HumanNameDt firstMiddleLastName = new HumanNameDt() {
    {
      setGiven(convertToStringDt(Arrays.asList("Gráinne", "Ní", "Jefferson")));
      setFamily(convertToStringDt(Arrays.asList("Mháille")));
    }
  };

  @Test
  public void formatFullName() {
    assertEquals(HumanNames.toFullName(firstMiddleLastName), "Gráinne Ní Jefferson Mháille");
  }

  @Test
  public void formatFirstMiddleLast() {
    assertEquals(HumanNames.toFirstMiddleLastName(firstMiddleLastName),
        "Gráinne Ní Mháille");
  }

  @Test
  public void formatLastFirstMiddleInitial() {
    assertEquals(HumanNames.toLastFirstMiddleInitial(firstMiddleLastName),
        "Mháille Gráinne N");
  }

  @Test
  public void nullTest() {
    assertEquals(HumanNames.toFullName(nullName), "");
  }

  @Test
  public void arabicTest() {
    assertEquals(HumanNames.toFullName(arabicName), "سعد الهمام");
  }

  @Test
  public void longNameTest() {
    assertEquals(HumanNames.toFullName(longName),
        "John Jacob Johnny JoeBob JimJoe JB Jezekiah Jammyjamjingle "
        + "Hallston Thurston Dunston Winston Fallstone Firestone Firestein Feuerstein "
        + "Rammstein Bierstein Dig-A-Dig-Dag Whopbopaloobopawapabamboom Loblolly Linden "
        + "Zeddemore Gambolputty de von Ausfern Sphlendenschlitt Digger Dagger "
        + "Dongle Tikolensic Grander Knotty Spelltinkle Grumblemeyer Luber Hundsfut Von "
        + "Hauptkopf of Ulm");
  }

  private List<StringDt> convertToStringDt(List<String> strings) {
    ArrayList<StringDt> stringDts = new ArrayList<>();
    for (String string : strings) {
      stringDts.add(new StringDt(string));
    }
    return stringDts;
  }
}
