// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Harms logic for VTE anticoagulation.
 */
@Slf4j
public class AnticoagulationImpl {
  private static final BigDecimal NEGATIVE_ONE = new BigDecimal("-1");
  private static final BigDecimal ZERO_POINT_EIGHT_SIX = new BigDecimal("0.86");

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Gets type of anticoagulant in use for an encounter.
   *
   * @param encounterId
   *     encounter to search
   * @return optional anticoagulant type, or empty if none.
   */
  public Optional<AnticoagulationTypeEnum> getAnticoagulationType(String encounterId) {
    List<MedicationAdministration> administrations = apiClient.getMedicationAdministrationClient()
        .search(encounterId);
    return getAnticoagulationType(administrations, encounterId);
  }

  /**
   * Gets type of anticoagulant in use for an encounter.
   *
   * @param administrations
   *     All medication administrations for an encounter.
   * @param encounterId
   *     encounter to search
   * @return optional anticoagulant type, or empty if none.
   */
  public Optional<AnticoagulationTypeEnum> getAnticoagulationType(
      Collection<MedicationAdministration> administrations, String encounterId) {
    administrations = MedicationAdministrationUtils.freshestOfAllOrders(administrations).values();

    for (MedicationAdministration admin : administrations) {
      for (IdentifierDt ident : MedicationAdministrationUtils.findIdentifiers(admin,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
        String medsSet = ident.getValue();
        for (AnticoagulationTypeEnum antiType : AnticoagulationTypeEnum.values()) {
          if (antiType.getCode().equals(medsSet)) {

            // Get duration for evaluating completed admins.
            Long period = HarmsLookups.efficacyList.get(medsSet);
            DateTimeDt timeTaken = (DateTimeDt) admin.getEffectiveTime();

            if (medsSet.contains("Continuous")) {
              if (MedicationAdministrationStatusEnum.IN_PROGRESS
                  .equals(admin.getStatusElement().getValueAsEnum())) {
                return Optional.of(antiType);
              } else if (MedicationAdministrationStatusEnum.COMPLETED
                  .equals(admin.getStatusElement().getValueAsEnum())
                  || MedicationAdministrationStatusEnum.ON_HOLD
                  .equals(admin.getStatusElement().getValueAsEnum())) {
                if (HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, clock)) {
                  return Optional.of(antiType);
                }
              }
            } else if (medsSet.contains("Intermittent")) {
              if (medsSet.equals(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN
                  .getCode())) {
                if (isEnoxaparinOverPoint86(admin, encounterId)) {
                  if (MedicationAdministrationStatusEnum.IN_PROGRESS
                      .equals(admin.getStatusElement().getValueAsEnum())) {
                    return Optional.of(antiType);
                  } else if (MedicationAdministrationStatusEnum.COMPLETED
                      .equals(admin.getStatusElement().getValueAsEnum())) {
                    if (HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, clock)) {
                      return Optional.of(antiType);
                    }
                  }
                }
              } else {
                // Intermittent but not Enoxaparin
                if (MedicationAdministrationStatusEnum.IN_PROGRESS
                    .equals(admin.getStatusElement().getValueAsEnum())) {
                  return Optional.of(antiType);
                } else if (MedicationAdministrationStatusEnum.COMPLETED
                    .equals(admin.getStatusElement().getValueAsEnum())) {
                  if (HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, clock)) {
                    return Optional.of(antiType);
                  }
                } // end if admin is in progress or completed
              } // end if intermittent is or isn't enoxaparin
            } // end if is or isn't intermittent
          } // end if meds set matches an anticoagulation type
        }
      }
    }

    return Optional.empty();
  }

  private boolean isEnoxaparinOverPoint86(MedicationAdministration admin, String encounterId) {
    BigDecimal dose = admin.getDosage().getQuantity().getValue();
    String unit = admin.getDosage().getQuantity().getUnit();

    if (dose == null || unit == null) {
      log.warn(
          "Retrieved null dosage in enoxaparin administration for encounter [{}],"
          + " affecting anticoagulation logic", encounterId);
      return false;
    }

    if ("mg/kg".equals(unit)) {
      return (dose.compareTo(ZERO_POINT_EIGHT_SIX) >= 0);
    } else if ("mg".equals(unit)) {
      BigDecimal weight = getPatientWeight(encounterId);
      if (weight.compareTo(NEGATIVE_ONE) == 0) {
        log.warn(
            "Failed to retrieve patient weight for enoxaparin dosage for encounter [{}], "
            + "affecting anticoagulation logic", encounterId);
        return false;
      } else {
        return (dose.divide(weight, 10, BigDecimal.ROUND_HALF_UP)
            .compareTo(ZERO_POINT_EIGHT_SIX) >= 0);
      }
    } else {
      log.warn(
          "Retrieved unrecognized dosage unit [{}]] for encounter [{}], "
          + "affecting anticoagulation logic", unit, encounterId);
      return false;
    }
  }

  /**
   * Wraps HarmsLookups patient weight method to facilitate unit testing.
   *
   * @param encounterId
   *     Encounter of the patient whose weight we want.
   * @return
   *     Patient weight in kg.
   */
  public BigDecimal getPatientWeight(String encounterId) {
    return HarmsLookups.getPatientWeight(apiClient, encounterId);
  }
}
