// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.Arrays;
import java.util.Date;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests Pharmacologic VTE Prophylaxis Administered logic without the API.
 */
public class PharmacologicVteProphylaxisAdministeredTest
    extends PharmacologicVteProphylaxisAdministered {

  private static final BigDecimal weight = new BigDecimal("100");

  /**
   * Test of isPharmacologicVteProphylaxisAdministered method, of
   * class PharmacologicVteProphylaxisAdministered.
   */
  @Test
  public void testIsPharmacologicVteProphylaxisAdministered() {
    MedicationAdministration enoxFail = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 1, "mg/kg");

    assertFalse(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxFail), "whatever"));

    MedicationAdministration enoxpass = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 85, "mg");

    assertTrue(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxpass), "whatever"));

    MedicationAdministration enoxFail2 = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 87, "mg");

    assertFalse(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxFail2), "whatever"));

    MedicationAdministration hepPass = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_HEPARIN_SC.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 900000, "mg");

    assertTrue(isPharmacologicVteProphylaxisAdministered(Arrays.asList(hepPass), "whatever"));
  }

  private MedicationAdministration createMedicationAdministration(String medsSet,
      MedicationAdministrationStatusEnum status, int dose, String unit) {

    MedicationAdministration administration = new MedicationAdministration();
    administration.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ADMINISTRATION)
        .setValue("whatever");
    administration.addIdentifier()
        .setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME)
        .setValue(medsSet);
    administration.setStatus(status);
    administration.setEffectiveTime(DateTimeDt.withCurrentTime());
    MedicationAdministration.Dosage dosage = new MedicationAdministration.Dosage();
    dosage.setQuantity(new SimpleQuantityDt(dose, "", unit));
    dosage.setRate(new RatioDt());
    administration.setDosage(dosage);
    return administration;
  }

  @Override
  public BigDecimal getPatientWeight(String encounterId) {
    return weight;
  }

  @Override
  public boolean withinDrugPeriod(Date timeTaken, long period, Clock clock) {
    return true;
  }
}
