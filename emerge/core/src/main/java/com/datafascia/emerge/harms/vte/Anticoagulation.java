// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.harms.HarmsLookups;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Harms logic for VTE anticoagulation.
 */
public class Anticoagulation {

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
        ResourceReferenceDt medicationReference =
            (ResourceReferenceDt) medicationOrder.getMedication();
        Medication medication = apiClient.getMedicationClient()
            .getMedication(medicationReference.getReference().getIdPart());
        String medName = medication.getCode().getText();

        for (AnticoagulationTypeEnum at : AnticoagulationTypeEnum.values()) {
          if (at.toString().toUpperCase().equals(medName.replace(" ", "_"))) {
            return Optional.of(at);
          }
        }
      }
    }

    List<MedicationAdministration> administrations = apiClient.getMedicationAdministrationClient()
        .search(encounterId);

    // Check if any recent administrations have been made that are anticoagulants.
    for (MedicationAdministration administration : administrations) {
      ResourceReferenceDt prescriptionReference = administration.getPrescription();

      MedicationOrder medicationOrder = apiClient.getMedicationOrderClient()
          .read(prescriptionReference.getReference().getIdPart(), encounterId);
      ResourceReferenceDt medicationReference =
          (ResourceReferenceDt) medicationOrder.getMedication();

      Medication medication = apiClient.getMedicationClient()
          .getMedication(medicationReference.getReference().getIdPart());
      String medName = medication.getCode().getText();

      for (AnticoagulationTypeEnum at : AnticoagulationTypeEnum.values()) {
        DateTimeDt timeTaken = (DateTimeDt) administration.getEffectiveTime();
        Long period = HarmsLookups.efficacyList.get(medName);

        if (at.toString().toUpperCase().equals(medName.replace(" ", "_"))
            && HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period)) {

          // If INR is greater than 1.5, then it's still active. Otherwise, return null.
          if (medName.equals("Warfarin")) {
            if (HarmsLookups.inrOver1point5(apiClient, encounterId)) {
              return Optional.of(at);
            } else {
              return Optional.empty();
            }
          }

          return Optional.of(at);
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
