// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.MedicationOrderUtils;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Harms logic for VTE anticoagulation.
 */
public class AnticoagulationImpl {

  @Inject
  private ClientBuilder apiClient;

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

    // First check active prescriptions for the anticoagulants.
    for (MedicationOrder medicationOrder : medicationOrders) {
      if (medicationOrder.getStatusElement().getValueAsEnum() == MedicationOrderStatusEnum.ACTIVE) {
        for (IdentifierDt ident : MedicationOrderUtils.findIdentifiers(medicationOrder,
            CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
          for (AnticoagulationTypeEnum atEnum : AnticoagulationTypeEnum.values()) {
            if (atEnum.getCode().equals(ident.getValue())) {
              return Optional.of(atEnum);
            }
          }
        }
      }
    }

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

          if (atEnum.getCode().equals(medsSet)
              && HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period)) {

            // If INR is greater than 1.5, then it's still active. Otherwise, return null.
            if (medsSet.equals("Intermittent Warfarin Enteral")) {
              if (HarmsLookups.inrOver1point5(apiClient, encounterId)) {
                return Optional.of(atEnum);
              } else {
                return Optional.empty();
              }
            }

            return Optional.of(atEnum);
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
    return getAnticoagulationType(encounterId).isPresent();
  }
}
