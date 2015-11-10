// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.util.Comparator;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * ProcedureRequest scheduled comparator test
 */
public class ProcedureRequestScheduledComparatorTest {

  private ProcedureRequest createProcedureRequest(DateTimeDt date) {
    PeriodDt period = new PeriodDt()
        .setStart(date);

    return new ProcedureRequest()
        .setScheduled(period);
  }

  @Test
  public void testCompare() {
    ProcedureRequest stale = createProcedureRequest(new DateTimeDt("2014-01-26T11:11:11"));
    ProcedureRequest fresh = createProcedureRequest(new DateTimeDt("2014-01-26T12:12:12"));
    ProcedureRequest nullRequest = null;
    ProcedureRequest emptyPeriod = new ProcedureRequest()
        .setScheduled(new PeriodDt());

    Comparator<ProcedureRequest> comparator = ProcedureRequestUtils.getScheduledComparator();

    assertEquals(comparator.compare(fresh, stale), 1);
    assertEquals(comparator.compare(fresh, fresh), 0);
    assertEquals(comparator.compare(stale, fresh), -1);
    assertEquals(comparator.compare(nullRequest, nullRequest), 0);
    assertEquals(comparator.compare(stale, nullRequest), 1);
    assertEquals(comparator.compare(nullRequest, fresh), -1);
    assertEquals(comparator.compare(emptyPeriod, nullRequest), 1);
    assertEquals(comparator.compare(stale, emptyPeriod), 1);
    assertEquals(comparator.compare(emptyPeriod, fresh), -1);
  }
}
