// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.model.dstu2.valueset.MedicationPrescriptionStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import java.util.List;

/**
 * Pharmacologic VTE Prophylaxis Ordered implementation
 */
public class PharmacologicVtePpx {
  /**
   * Pharmacologic VTE Prophylaxis Type Implementation
   *
   * @param client
   *     the FHIR client used to search Topaz
   * @param encounterId
   *     the Encounter to search
   * @return type
   *     the specific pharmacologic VTE prophylaxis ordered
   */
  public static String pharmacologicVtePpxType(ClientBuilder client, String encounterId) {
    String type = null;

    List<MedicationPrescription> prescriptions = client.getMedicationPrescriptionClient()
        .getMedicationPrescriptions(encounterId);
    for (MedicationPrescription prescription : prescriptions) {
      if (prescription.getStatus().equals(MedicationPrescriptionStatusEnum.ACTIVE.getCode())) {
        if ((prescription.getIdentifier().get(0).getValue().equals("Intermittent Enoxaparin SC")) ||
            (prescription.getIdentifier().get(0).getValue().equals("Intermittent Heparin SC"))) {
          type = prescription.getIdentifierFirstRep().getValue();
        }
      }
    }
    return type;
  }

  /**
   * Pharmacologic VTE Prophylaxis Type Implementation
   *
   * @param client
   *     the FHIR client used to query Topaz
   * @param encounterId
   *     the Encounter to search
   * @return type
   *     the specific pharmacologic VTE prophylaxis ordered
   */
  public static boolean pharmacologicVtePpxOrdered(ClientBuilder client, String encounterId) {
    return pharmacologicVtePpxType(client, encounterId) != null;
  }
}
