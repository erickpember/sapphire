// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.harms.HarmsLookups;
import java.util.List;
import javax.inject.Inject;

/**
 * Pharmacologic VTE Prophylaxis Administered implementation
 */
public class PharmacologicVteProphylaxisAdministered {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if pharmacologic VTE prophylaxis was administered
   *
   * @param encounterId
   *     encounter to search
   * @return true if VTE prophylaxis was administered
   */
  public boolean isPharmacologicVteProphylaxisAdministered(String encounterId) {
    boolean administered = false;

    List<MedicationAdministration> administrations = apiClient.getMedicationAdministrationClient()
        .search(encounterId);

    // Check if any recent VTE prophylactic administrations have been made.
    for (MedicationAdministration administration : administrations) {
      ResourceReferenceDt prescriptionReference = administration.getPrescription();
      MedicationOrder medicationOrder = apiClient.getMedicationOrderClient()
          .read(prescriptionReference.getReference().getIdPart(), encounterId);

      ResourceReferenceDt medicationReference =
          (ResourceReferenceDt) medicationOrder.getMedication();
      Medication medication = apiClient.getMedicationClient()
          .getMedication(medicationReference.getReference().getIdPart());

      String medicationName = medication.getCode().getText();
      for (PharmacologicVtePpxTypeEnum vtePpx : PharmacologicVtePpxTypeEnum.values()) {
        DateTimeDt timeTaken = (DateTimeDt) administration.getEffectiveTime();
        Long period = HarmsLookups.efficacyList.get(medicationName);
        if (vtePpx.toString().equalsIgnoreCase(medicationName.replace(" ", "_"))
            && HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period)) {
          administered = true;
          break;
        }
      }
    }

    return administered;
  }
}
