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
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests of SCDs ordered logic independent of API
 */
public class SCDsOrderedTest extends SCDsOrdered {

  /**
   * Test of isSCDsOrdered method, of class SCDsOrdered.
   */
  @Test
  public void testIsSCDsOrdered_List() {
    Instant now = Instant.now();
    Instant twoHoursAgo = now.minus(2, ChronoUnit.HOURS);
    Instant threeHoursAgo = now.minus(3, ChronoUnit.HOURS);

    DateTimeDt oldTime = new DateTimeDt(Date.from(threeHoursAgo));
    DateTimeDt newTime = new DateTimeDt(Date.from(twoHoursAgo));

    PeriodDt oldPeriod = new PeriodDt();
    oldPeriod.setStartWithSecondsPrecision(Date.from(threeHoursAgo));
    PeriodDt newPeriod = new PeriodDt();
    newPeriod.setStartWithSecondsPrecision(Date.from(twoHoursAgo));

    ProcedureRequest oldMaintainTime
        = createProcedureRequest(ProcedureRequestCodeEnum.MAINTAIN_SCDS.getCode(), oldTime);

    ProcedureRequest newRemoveTime
        = createProcedureRequest(ProcedureRequestCodeEnum.REMOVE_SCDS.getCode(), newTime);

    assertTrue(isSCDsOrdered(Arrays.asList(oldMaintainTime)));
    assertFalse(isSCDsOrdered(Arrays.asList(oldMaintainTime, newRemoveTime)));

    ProcedureRequest oldPlacePeriod
        = createProcedureRequest(ProcedureRequestCodeEnum.PLACE_SCDS.getCode(), oldPeriod);

    ProcedureRequest newRemovePeriod
        = createProcedureRequest(ProcedureRequestCodeEnum.REMOVE_SCDS.getCode(), newPeriod);

    assertTrue(isSCDsOrdered(Arrays.asList(oldPlacePeriod)));
    assertFalse(isSCDsOrdered(Arrays.asList(oldPlacePeriod, newRemovePeriod)));
  }

  private ProcedureRequest createProcedureRequest(String typeCode, IDatatype scheduled) {
    ProcedureRequest procedurerequest = new ProcedureRequest()
        .setCode(new CodeableConceptDt(CodingSystems.PROCEDURE_REQUEST, typeCode))
        .setScheduled(scheduled)
        .setStatus((ProcedureRequestStatusEnum)null)
        .setOrderedOn(new DateTimeDt(new Date(), TemporalPrecisionEnum.SECOND));
    return procedurerequest;
  }
}
