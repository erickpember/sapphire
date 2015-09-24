// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.harms.HarmsLookups;
import java.util.List;

/**
 * Pharmacologic VTE Prophylaxis Administered implementation
 */
public class PharmacologicVtePpxAdministered {
  /**
   * Pharmacologic VTE Prophylaxis Administered Implementation
   *
   * @param client
   *     the FHIR client used to query Topaz
   * @param encounterId
   *     the Encounter to search
   * @return administered
   *     indicates whether VTE prophylaxis have been administered
   */
  public static boolean pharmacologicVtePpxAdministered(ClientBuilder client, String encounterId) {
    boolean administered = false;
    List<MedicationAdministration> administrations = client.getMedicationAdministrationClient()
        .getMedicationAdministrations(encounterId);

    // Check if any recent VTE prophylactic administrations have been made.
    for (MedicationAdministration administration : administrations) {
      ResourceReferenceDt prescriptionReference = administration.getPrescription();
      MedicationPrescription prescription = client.getMedicationPrescriptionClient()
          .getMedicationPrescription(prescriptionReference.getElementSpecificId(), encounterId);
      Medication medication = client.getMedicationClient()
          .getMedication(prescription.getMedication().getReference().getValue());
      String medName = medication.getName();
      for (PharmacologicVtePpxTypeEnum vtePpx : PharmacologicVtePpxTypeEnum.values()) {
        DateTimeDt timeTaken = (DateTimeDt) administration.getEffectiveTime();
        Long period = HarmsLookups.efficacyList.get(medName);
        if (vtePpx.toString().equalsIgnoreCase(medName.replace(" ", "_"))
            && HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period)) {
          administered = true;
          break;
        }
      }
    }

    return administered;
  }
}
