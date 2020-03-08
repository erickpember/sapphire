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
