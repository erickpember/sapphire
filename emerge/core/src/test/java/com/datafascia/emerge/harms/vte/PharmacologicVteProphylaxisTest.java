// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.math.BigDecimal;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Tests Pharmacologic VTE Prophylaxis logic without the API.
 */
public class PharmacologicVteProphylaxisTest extends PharmacologicVteProphylaxis {
  private static final BigDecimal weight = new BigDecimal("100");

  public PharmacologicVteProphylaxisTest() {
  }

  /**
   * Test of getPharmacologicVteProphylaxisType method, of class PharmacologicVteProphylaxis.
   */
  @Test
  public void testGetPharmacologicVteProphylaxisType() {
    MedicationOrder enoxFail = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        1, "mg/kg");

    assertFalse(getPharmacologicVteProphylaxisType(Arrays.asList(enoxFail), "whatever")
        .isPresent());

    MedicationOrder enoxPass1 = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        85, "mg");

    assertEquals(getPharmacologicVteProphylaxisType(Arrays.asList(enoxPass1), "whatever").get(),
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode());

    MedicationOrder enoxFail2 = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        87, "mg");

    assertFalse(getPharmacologicVteProphylaxisType(Arrays.asList(enoxFail2), "whatever")
        .isPresent());

    MedicationOrder enoxFail3 = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        85, null);

    assertFalse(getPharmacologicVteProphylaxisType(Arrays.asList(enoxFail3), "whatever")
        .isPresent());

    MedicationOrder hepPass = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_HEPARIN_SC.getCode(),
        85, "mg");

    assertEquals(getPharmacologicVteProphylaxisType(Arrays.asList(hepPass), "whatever").get(),
        PharmacologicVtePpxTypeEnum.INTERMITTENT_HEPARIN_SC.getCode());
  }

  private MedicationOrder createMedicationOrder(
      String identifier, int dose, String unit) {

    MedicationOrder prescription = new MedicationOrder();
    prescription.addIdentifier()
        .setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME)
        .setValue(identifier);
    prescription.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ORDER)
        .setValue(identifier);
    prescription.setStatus(MedicationOrderStatusEnum.ACTIVE);
    MedicationOrder.DosageInstruction dosage = new MedicationOrder.DosageInstruction();
    dosage.setDose(new SimpleQuantityDt(dose, "", unit));
    prescription.setDosageInstruction(Arrays.asList(dosage));
    return prescription;
  }

  @Override
  public BigDecimal getPatientWeight(String encounterId) {
    return weight;
  }
}
