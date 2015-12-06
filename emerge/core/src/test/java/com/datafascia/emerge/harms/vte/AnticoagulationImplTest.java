// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.math.BigDecimal;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Tests Anticoagulation logic without the API.
 */
public class AnticoagulationImplTest extends AnticoagulationImpl {

  private static final BigDecimal weight = new BigDecimal("100");

  /**
   * Test of getAnticoagulationType method, of class AnticoagulationImpl.
   */
  @Test
  public void testGetAnticoagulationType_List_String() {
    MedicationAdministration enoxPass = createMedicationAdministration(
        AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 1, "mg/kg");

    assertEquals(getAnticoagulationType(Arrays.asList(enoxPass), "whatever").get(),
        AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN);

    MedicationAdministration enoxPass2 = createMedicationAdministration(
        AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 100, "mg");

    assertEquals(getAnticoagulationType(Arrays.asList(enoxPass2), "whatever").get(),
        AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN);

    MedicationAdministration enoxFail = createMedicationAdministration(
        AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 85, "mg");

    assertFalse(getAnticoagulationType(Arrays.asList(enoxFail), "whatever").isPresent());

    MedicationAdministration argaPass = createMedicationAdministration(
        AnticoagulationTypeEnum.CONTINUOUS_INFUSION_ARGATROBAN_IV.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 85, "mg");

    assertEquals(getAnticoagulationType(Arrays.asList(argaPass), "whatever").get(),
        AnticoagulationTypeEnum.CONTINUOUS_INFUSION_ARGATROBAN_IV);
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

}