// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
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

    assertTrue(isSCDsOrdered(Arrays.asList(oldMaintainTime), threeHoursAgo, null));
    assertFalse(isSCDsOrdered(Arrays.asList(oldMaintainTime, newRemoveTime), threeHoursAgo, null));

    // tests the lower bound
    assertFalse(isSCDsOrdered(Arrays.asList(oldMaintainTime), now, null));

    ProcedureRequest oldPlacePeriod
        = createProcedureRequest(ProcedureRequestCodeEnum.PLACE_SCDS.getCode(), oldPeriod);

    ProcedureRequest newRemovePeriod
        = createProcedureRequest(ProcedureRequestCodeEnum.REMOVE_SCDS.getCode(), newPeriod);

    assertTrue(isSCDsOrdered(Arrays.asList(oldPlacePeriod), threeHoursAgo, null));
    assertFalse(isSCDsOrdered(Arrays.asList(oldPlacePeriod, newRemovePeriod), threeHoursAgo, null));
  }

  private ProcedureRequest createProcedureRequest(String typeCode, IDatatype scheduled) {
    ProcedureRequest procedurerequest = new ProcedureRequest()
        .setCode(new CodeableConceptDt(CodingSystems.PROCEDURE_REQUEST, typeCode))
        .setScheduled(scheduled)
        .setOrderedOn(new DateTimeDt(new Date(), TemporalPrecisionEnum.SECOND));
    return procedurerequest;
  }
}
