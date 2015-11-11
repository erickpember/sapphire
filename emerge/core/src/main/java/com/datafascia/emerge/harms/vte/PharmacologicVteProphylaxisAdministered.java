// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.harms.HarmsLookups;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
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
      for (IdentifierDt ident : MedicationAdministrationUtils.findIdentifiers(administration,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME)) {
        String medsSet = ident.getValue();

        for (PharmacologicVtePpxTypeEnum vtePpx : PharmacologicVtePpxTypeEnum.values()) {
          DateTimeDt timeTaken = (DateTimeDt) administration.getEffectiveTime();
          Long period = HarmsLookups.efficacyList.get(medsSet);
          if (vtePpx.toString().equalsIgnoreCase(medsSet.replace(" ", "_"))
              && HarmsLookups.withinDrugPeriod(timeTaken.getValue(), period)) {
            administered = true;
            break;
          }
        }
      }
    }

    return administered;
  }
}
