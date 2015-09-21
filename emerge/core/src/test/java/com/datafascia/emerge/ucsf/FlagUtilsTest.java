// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
