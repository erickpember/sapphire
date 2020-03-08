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
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for FlagUtils
 */
@Slf4j
public class FlagUtilsTest {

  public FlagUtilsTest() {
  }

  /**
   * Test of findFreshestFlag method, of class FlagUtils.
   */
  @Test
  public void testFindFreshestFlag() {
    Flag stale = createFlag(new DateTimeDt("2014-01-26T11:11:11"), "stale");
    Flag fresh = createFlag(new DateTimeDt("2014-01-26T12:12:12"), "fresh");
    ArrayList<Flag> flags = new ArrayList<>(Arrays.asList(stale, fresh));
    assertEquals(FlagUtils.findFreshestFlag(flags).getId().getValue(),
        "fresh");
  }

  private Flag createFlag(DateTimeDt date, String id) {
    Flag flag = new Flag();
    PeriodDt period = new PeriodDt();
    period.setStart(date);
    flag.setId(new IdDt(id));
    flag.setPeriod(period);
    return flag;
  }
}
