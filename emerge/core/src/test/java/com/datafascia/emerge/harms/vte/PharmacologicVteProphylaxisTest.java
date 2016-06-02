// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
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
    Instant now = Instant.now();

    MedicationOrder enoxFail = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        1, "mg/kg", now);

    assertFalse(getPharmacologicVteProphylaxisType(Arrays.asList(enoxFail), "whatever", now)
        .isPresent());

    MedicationOrder enoxPass1 = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        85, "mg", now);

    assertEquals(getPharmacologicVteProphylaxisType(Arrays.asList(enoxPass1), "whatever", now)
        .get(), PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode());

    MedicationOrder enoxFail2 = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        87, "mg", now);

    assertFalse(getPharmacologicVteProphylaxisType(Arrays.asList(enoxFail2), "whatever", now)
        .isPresent());

    MedicationOrder enoxFail3 = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        85, null, now);

    assertFalse(getPharmacologicVteProphylaxisType(Arrays.asList(enoxFail3), "whatever", now)
        .isPresent());

    MedicationOrder hepPass = createMedicationOrder(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_HEPARIN_SC.getCode(),
        85, "mg", now);

    assertEquals(getPharmacologicVteProphylaxisType(Arrays.asList(hepPass), "whatever", now)
        .get(), PharmacologicVtePpxTypeEnum.INTERMITTENT_HEPARIN_SC.getCode());
  }

  private MedicationOrder createMedicationOrder(
      String identifier, int dose, String unit, Instant now) {

    MedicationOrder prescription = new MedicationOrder();
    prescription.setDateWritten(new DateTimeDt(Date.from(now)));
    prescription.setDateEnded(new DateTimeDt(Date.from(Instant.EPOCH)));
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
