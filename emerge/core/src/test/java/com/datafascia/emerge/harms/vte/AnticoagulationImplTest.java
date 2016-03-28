// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.emerge.testUtils.TestResources;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
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
    ClientBuilder apiClient = TestResources.createMockClient();

    MedicationAdministration enoxPass = TestResources.createMedicationAdministration(
        "id",
        Arrays.asList(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        1,
        "mg/kg",
        DateTimeDt.withCurrentTime(),
        "activeOrder");

    assertEquals(getAnticoagulationType(Arrays.asList(enoxPass), "encounterId", Instant
        .now(),
        apiClient).get(),
        AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN);

    MedicationAdministration enoxPass2 = TestResources.createMedicationAdministration(
        "id",
        Arrays.asList(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        100,
        "mg",
        DateTimeDt.withCurrentTime(),
        "activeOrder");

    assertEquals(getAnticoagulationType(Arrays.asList(enoxPass2), "encounterId", Instant
        .now(),
        apiClient).get(),
        AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN);

    MedicationAdministration enoxPass3 = TestResources.createMedicationAdministration(
        "id",
        Arrays.asList(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        100,
        "mg",
        DateTimeDt.withCurrentTime(),
        "completedOrder");

    assertEquals(getAnticoagulationType(Arrays.asList(enoxPass3), "encounterId", Instant
        .now(),
        apiClient).get(),
        AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN);

    MedicationAdministration enoxFail = TestResources.createMedicationAdministration(
        "id",
        Arrays.asList(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        85,
        "mg",
        DateTimeDt.withCurrentTime(),
        "activeOrder");

    assertFalse(
        getAnticoagulationType(Arrays.asList(enoxFail), "encounterId", Instant.now(),
            apiClient)
        .isPresent());

    MedicationAdministration argaPass = TestResources.createMedicationAdministration(
        "id",
        Arrays.asList(AnticoagulationTypeEnum.CONTINUOUS_INFUSION_ARGATROBAN_IV.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        85,
        "mg",
        DateTimeDt.withCurrentTime(),
        "activeOrder");

    assertEquals(getAnticoagulationType(Arrays.asList(argaPass), "encounterId", Instant
        .now(),
        apiClient).get(),
        AnticoagulationTypeEnum.CONTINUOUS_INFUSION_ARGATROBAN_IV);

    MedicationAdministration hepFailTooOldCompletedOrder = TestResources
        .createMedicationAdministration(
            "id",
            Arrays.asList(AnticoagulationTypeEnum.CONTINUOUS_INFUSION_HEPARIN_IV.getCode()),
            MedicationAdministrationStatusEnum.IN_PROGRESS,
            85,
            "mg",
            new DateTimeDt(Date.from(Instant.now().minus(15, ChronoUnit.HOURS))),
            "completedOrder");

    assertFalse(getAnticoagulationType(Arrays.asList(hepFailTooOldCompletedOrder),
        "encounterId",
        Instant.now(),
        apiClient)
        .isPresent());

    MedicationAdministration hepPassOldButActiveOrder = TestResources
        .createMedicationAdministration(
            "id",
            Arrays.asList(AnticoagulationTypeEnum.CONTINUOUS_INFUSION_HEPARIN_IV.getCode()),
            MedicationAdministrationStatusEnum.IN_PROGRESS,
            85,
            "mg",
            new DateTimeDt(Date.from(Instant.now().minus(15, ChronoUnit.HOURS))),
            "activeOrder");

    assertEquals(getAnticoagulationType(Arrays.asList(hepPassOldButActiveOrder),
        "encounterId",
        Instant.now(),
        apiClient).get(),
        AnticoagulationTypeEnum.CONTINUOUS_INFUSION_HEPARIN_IV);

    MedicationAdministration hepFailNewWithCompletedOrder = TestResources
        .createMedicationAdministration(
            "id",
            Arrays.asList(AnticoagulationTypeEnum.CONTINUOUS_INFUSION_HEPARIN_IV.getCode()),
            MedicationAdministrationStatusEnum.IN_PROGRESS,
            85,
            "mg",
            new DateTimeDt(Date.from(Instant.now().minus(15, ChronoUnit.HOURS))),
            "completedOrder");

    assertFalse(getAnticoagulationType(Arrays.asList(hepFailNewWithCompletedOrder),
        "encounterId",
        Instant.now(),
        apiClient).isPresent());
  }

  @Override
  public BigDecimal getPatientWeight(String encounterId) {
    return weight;
  }

  private MedicationOrder createMedicationOrder() {
    MedicationOrder order = new MedicationOrder();
    order.addIdentifier().setValue("activeOrder")
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ORDER);
    order.setStatus(MedicationOrderStatusEnum.ACTIVE);
    return order;
  }

}
