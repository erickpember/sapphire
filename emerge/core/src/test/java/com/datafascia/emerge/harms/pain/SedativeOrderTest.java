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
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test of SedativeOrder
 */
public class SedativeOrderTest {
  private static final Set<String> SEDATIVE_MEDS_SETS = ImmutableSet.of(
      MedsSetEnum.CONTINUOUS_INFUSION_LORAZEPAM_IV.getCode(),
      MedsSetEnum.CONTINUOUS_INFUSION_MIDAZOLAM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_ALPRAZALOM_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_CHLORADIAZEPOXIDE_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_CLONAZEPAM_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_DIAZEPAM_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_DIAZEPAM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_LORAZEPAM_ENTERAL.getCode(),
      MedsSetEnum.INTERMITTENT_LORAZEPAM_IV.getCode(),
      MedsSetEnum.INTERMITTENT_MIDAZOLAM_IV.getCode());

  /**
   * Test of processSedativeOrders method, of class SedativeOrder.
   */
  @Test
  public void testProcessOrders() {

    SedativeOrder sedativeOrderImpl = new SedativeOrder();

    List<MedicationOrder> orders = new ArrayList<>();

    for (String benzo : SEDATIVE_MEDS_SETS) {
      // These are valid.
      orders.add(createMedicationOrder(benzo, MedicationOrderStatusEnum.ACTIVE));
      orders.add(createMedicationOrder(benzo, MedicationOrderStatusEnum.DRAFT));
      // We are counting orders with null statuses.
      orders.add(createMedicationOrder(benzo, null));

      // These are invalid.
      orders.add(createMedicationOrder(null, null));
      orders.add(createMedicationOrder(benzo, MedicationOrderStatusEnum.COMPLETED));
      orders.add(createMedicationOrder(benzo, MedicationOrderStatusEnum.ENTERED_IN_ERROR));
      orders.add(createMedicationOrder(benzo, MedicationOrderStatusEnum.STOPPED));
      orders.add(createMedicationOrder("not a benzo", MedicationOrderStatusEnum.STOPPED));
    }

    assertEquals(sedativeOrderImpl.processSedativeOrders(orders).size(), 20);
  }

  private MedicationOrder createMedicationOrder(
      String identifier, MedicationOrderStatusEnum status) {

    MedicationOrder prescription = new MedicationOrder();
    prescription.addIdentifier()
        .setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME)
        .setValue(identifier);
    prescription.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ORDER)
        .setValue(identifier);
    prescription.setStatus(status);
    return prescription;
  }
}
