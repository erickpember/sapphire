// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link ProcedureRequestUtils} test
 */
public class ProcedureRequestUtilsTest {

  /**
   * Test of findFreshestProcedureRequest method, of class ProcedureRequestUtils.
   */
  @Test
  public void testFindFreshestProcedureRequest() {
    ProcedureRequest stale = createProcedureRequest(new DateTimeDt("2014-01-26T11:11:11"), "stale");
    ProcedureRequest fresh = createProcedureRequest(new DateTimeDt("2014-01-26T12:12:12"), "fresh");
    ArrayList<ProcedureRequest> requests = new ArrayList<>(Arrays.asList(stale, fresh));
    assertEquals(ProcedureRequestUtils.findFreshestProcedureRequest(requests).getId().getValue(),
        "fresh");
  }

  private ProcedureRequest createProcedureRequest(DateTimeDt date, String id) {
    ProcedureRequest request = new ProcedureRequest();
    request.setId(new IdDt(id));
    request.setScheduled(date);
    return request;
  }
}
