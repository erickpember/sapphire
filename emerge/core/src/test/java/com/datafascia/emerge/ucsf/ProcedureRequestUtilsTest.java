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
