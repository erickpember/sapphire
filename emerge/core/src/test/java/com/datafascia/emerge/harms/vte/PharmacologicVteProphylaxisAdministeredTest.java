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
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

  private static final BigDecimal WEIGHT = new BigDecimal("100");

  /**
   * Test of isPharmacologicVteProphylaxisAdministered method, of
   * class PharmacologicVteProphylaxisAdministered.
   */
  @Test
  public void testIsPharmacologicVteProphylaxisAdministered() {
    Instant now = Instant.now();

    MedicationAdministration enoxFail = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 1, "mg/kg");

    assertFalse(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxFail), "whatever",
        now));

    MedicationAdministration enoxpass = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 85, "mg");

    assertTrue(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxFail, enoxpass),
        "whatever", now));

    MedicationAdministration enoxFail2 = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 87, "mg");

    assertFalse(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxFail2), "whatever",
        now));

    DateTimeDt tooLongAgo = new DateTimeDt(Date.from(now.minus(26, ChronoUnit.HOURS)));
    MedicationAdministration enoxFail3 = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_ENOXAPARIN.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 85, "mg", tooLongAgo);

    assertFalse(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxFail3), "whatever",
        now));

    assertTrue(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxFail3, enoxpass),
        "whatever", now));

    assertTrue(isPharmacologicVteProphylaxisAdministered(Arrays.asList(enoxpass, enoxFail3),
        "whatever", now));


    MedicationAdministration hepPass = createMedicationAdministration(
        PharmacologicVtePpxTypeEnum.INTERMITTENT_HEPARIN_SC.getCode(),
        MedicationAdministrationStatusEnum.IN_PROGRESS, 900000, "mg");

    assertTrue(isPharmacologicVteProphylaxisAdministered(Arrays.asList(hepPass), "whatever",
        now));
  }

  private MedicationAdministration createMedicationAdministration(String medsSet,
      MedicationAdministrationStatusEnum status, int dose, String unit) {
    return createMedicationAdministration(medsSet, status, dose, unit,
        DateTimeDt.withCurrentTime());
  }

  private MedicationAdministration createMedicationAdministration(String medsSet,
      MedicationAdministrationStatusEnum status, int dose, String unit, DateTimeDt effectiveTime) {

    MedicationAdministration administration = new MedicationAdministration();
    administration.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ADMINISTRATION)
        .setValue("whatever");
    administration.addIdentifier()
        .setSystem(CodingSystems.UCSF_MEDICATION_GROUP_NAME)
        .setValue(medsSet);
    administration.setStatus(status);
    administration.setEffectiveTime(effectiveTime);
    MedicationAdministration.Dosage dosage = new MedicationAdministration.Dosage();
    dosage.setQuantity(new SimpleQuantityDt(dose, "", unit));
    dosage.setRate(new RatioDt());
    administration.setDosage(dosage);
    return administration;
  }

  @Override
  public BigDecimal getPatientWeight(String encounterId) {
    return WEIGHT;
  }
}
