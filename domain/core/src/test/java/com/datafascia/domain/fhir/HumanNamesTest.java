// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
