// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
