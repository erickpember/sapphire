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

import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import java.util.ArrayList;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link MedicationOrderUtils} test
 */
public class MedicationOrderUtilsTest {

  /**
   * Test of findFreshestMedicationOrder method, of class MedicationOrderUtils.
   */
  @Test
  public void testFindFreshestMedicationOrder() {
    MedicationOrder stale = createMedicationOrder(new DateTimeDt("2014-01-26T11:11:11"), "stale",
        MedicationOrderStatusEnum.ACTIVE);
    MedicationOrder fresh = createMedicationOrder(new DateTimeDt("2014-01-26T12:12:12"), "fresh",
        MedicationOrderStatusEnum.STOPPED);
    ArrayList<MedicationOrder> orders = new ArrayList<>(Arrays.asList(stale, fresh));
    assertEquals(MedicationOrderUtils.findFreshestMedicationOrder(orders).getId().getValue(),
        "fresh");
  }

  private MedicationOrder createMedicationOrder(DateTimeDt date, String id,
      MedicationOrderStatusEnum status) {
    MedicationOrder order = new MedicationOrder();
    order.setId(new IdDt(id));
    order.setStatus(status);
    order.setDateWritten(date);
    return order;
  }
}
