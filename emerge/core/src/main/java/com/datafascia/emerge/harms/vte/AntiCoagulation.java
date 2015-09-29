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

/**
 * Harms logic for VTE anti-coagulation.
 */
public class AntiCoagulation {

  /**
   * What type of anti-coagulant is in use for this encounter. Null if none.
   *
   * @param client The client builder to use.
   * @param encounterId The encounter to check.
   * @return The anti-coagulant type. Null if none.
   */
  public static AnticoagulationTypeEnum getAnticoagulationTypeForEncounter(ClientBuilder client,
      String encounterId) {
    List<MedicationOrder> medicationOrders = client.getMedicationOrderClient()
        .search(encounterId);

    // First check active prescriptions for the anticoagulants.
    for (MedicationOrder medicationOrder : medicationOrders) {
      if (medicationOrder.getStatusElement().getValueAsEnum() == MedicationOrderStatusEnum.ACTIVE) {
        ResourceReferenceDt medicationReference =
            (ResourceReferenceDt) medicationOrder.getMedication();
        Medication medication = client.getMedicationClient()
            .getMedication(medicationReference.getReference().getIdPart());
        String medName = medication.getCode().getText();

        for (AnticoagulationTypeEnum at : AnticoagulationTypeEnum.values()) {
          if (at.toString().toUpperCase().equals(medName.replace(" ", "_"))) {
            return at;
          }
        }
      }
    }

    List<MedicationAdministration> administrations = client.getMedicationAdministrationClient()
        .getMedicationAdministrations(encounterId);

    // Check if any recent administrations have been made that are anticoagulants.
    for (MedicationAdministration administration : administrations) {
      ResourceReferenceDt prescriptionReference = administration.getPrescription();

      MedicationOrder medicationOrder = client.getMedicationOrderClient()
          .read(prescriptionReference.getReference().getIdPart(), encounterId);
      ResourceReferenceDt medicationReference =
          (ResourceReferenceDt) medicationOrder.getMedication();

      Medication medication = client.getMedicationClient()
          .getMedication(medicationReference.getReference().getIdPart());
      String medName = medication.getCode().getText();

      for (AnticoagulationTypeEnum at : AnticoagulationTypeEnum.values()) {
        DateTimeDt timeTaken = (DateTimeDt) administration.getEffectiveTime();
        Long period = HarmsLookups.efficacyList.get(medName);

        if (at.toString().toUpperCase().equals(medName.replace(" ", "_"))
            && HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period)) {

          // If INR is greater than 1.5, then it's still active. Otherwise, return null.
          if (medName.equals("Warfarin")) {
            if (HarmsLookups.inrOver1point5(client, encounterId)) {
              return at;
            } else {
              return null;
            }
          }

          return at;
        }
      }
    }

    return null;
  }

  /**
   * Is the patient on an anti-coagulant?
   *
   * @param client The client builder to use.
   * @param encounterId The encounter ID to use.
   * @return If the patient is anti-coagulated.
   */
  public static boolean isAntiCoagulated(ClientBuilder client, String encounterId) {
    return getAnticoagulationTypeForEncounter(client, encounterId) != null;
  }
}
