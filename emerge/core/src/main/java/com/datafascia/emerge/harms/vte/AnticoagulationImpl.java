// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.MedicationOrderUtils;
import java.math.BigDecimal;
import java.time.Clock;
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
    List<MedicationOrder> medicationOrders = apiClient.getMedicationOrderClient()
        .search(encounterId);

    for (MedicationOrder medicationOrder : medicationOrders) {
      if (MedicationOrderUtils.isActiveOrDraft(medicationOrder)) {
        for (IdentifierDt ident : MedicationOrderUtils.findIdentifiers(medicationOrder,
            CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
          for (AnticoagulationTypeEnum atEnum : AnticoagulationTypeEnum.values()) {
            if (atEnum.getCode().equals(ident.getValue())) {

              // Check dose ratio for Intermittent Enoxaparin SC
              if (ident.getValue().equals(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN_SC
                  .getCode())) {
                MedicationOrder.DosageInstruction dosage =
                    medicationOrder.getDosageInstructionFirstRep();
                IDatatype dose = dosage.getDose();
                if (dose instanceof QuantityDt) {
                  QuantityDt quantity = (QuantityDt) dosage.getDose();
                  BigDecimal weight = HarmsLookups.getPatientWeight(apiClient, encounterId);
                  if (weight.compareTo(NEGATIVE_ONE) == 0) {
                    log.error(
                        "Failed to retrieve patient weight for enoxaparin dosage for medOrder [{}] "
                            + "encounter [{}]",
                        medicationOrder.getIdentifierFirstRep().getValue(), encounterId);
                    return Optional.empty();
                  } else {
                    return quantity.getValue().divide(weight, 10, BigDecimal.ROUND_HALF_UP)
                        .compareTo(ZERO_POINT_EIGHT_SIX) > -1
                        ? Optional.of(atEnum) : Optional.empty();
                  }
                } else {
                  log.error("Dose is not of QuantityDt for medOrder [{}] encounter [{}]",
                      medicationOrder.getIdentifierFirstRep().getValue(), encounterId);
                  return Optional.empty();
                }
              } else {
                return Optional.of(atEnum);
              }
            }
          }
        }
      }
    }

    return Optional.empty();
  }

  /**
   * Checks if the patient on an anticoagulant.
   *
   * @param encounterId
   *     encounter ID to search
   * @return true if the patient is anticoagulated.
   */
  public boolean isAnticoagulated(String encounterId) {
    List<MedicationAdministration> administrations = apiClient.getMedicationAdministrationClient()
        .search(encounterId);

    // Check if any recent administrations have been made that are anticoagulants.
    for (MedicationAdministration administration : administrations) {
      for (IdentifierDt ident : MedicationAdministrationUtils.findIdentifiers(administration,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
        String medsSet = ident.getValue();

        for (AnticoagulationTypeEnum atEnum : AnticoagulationTypeEnum.values()) {
          DateTimeDt timeTaken = (DateTimeDt) administration.getEffectiveTime();
          Long period = HarmsLookups.efficacyList.get(medsSet);

          if (atEnum.getCode().equals(medsSet) &&
              HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period, clock) &&
              (MedicationAdministrationStatusEnum.COMPLETED
                   .equals(administration.getStatusElement().getValueAsEnum()) ||
               MedicationAdministrationStatusEnum.IN_PROGRESS
                   .equals(administration.getStatusElement().getValueAsEnum()))) {

            // If INR is greater than 1.5, then it's still active. Otherwise, return null.
            if (medsSet.equals(AnticoagulationTypeEnum.INTERMITTENT_WARFARIN_ENTERAL.getCode())) {
              if (HarmsLookups.inrOver1point5(apiClient, encounterId)) {
                return true;
              } else {
                return false;
              }

            // Check dose ratio for Enoxaparin SC
            } else if (medsSet.equals(AnticoagulationTypeEnum.INTERMITTENT_ENOXAPARIN_SC
                .getCode())) {
              BigDecimal dose = administration.getDosage().getQuantity().getValue();
              BigDecimal weight = HarmsLookups.getPatientWeight(apiClient, encounterId);
              if (weight.compareTo(NEGATIVE_ONE) == 0) {
                log.error(
                    "Failed to retrieve patient weight for enoxaparin dosage for encounter [{}]",
                    encounterId);
                return false;
              } else {
                return dose.divide(weight, 10, BigDecimal.ROUND_HALF_UP)
                    .compareTo(ZERO_POINT_EIGHT_SIX) >= 0;
              }
            } else {
              return true;
            }
          }
        }
      }
    }

    return false;
  }
}
