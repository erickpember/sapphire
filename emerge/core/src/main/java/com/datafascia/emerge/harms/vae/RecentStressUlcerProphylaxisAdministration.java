// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.datafascia.emerge.ucsf.codes.MedicationOrderEnum.STRESS_ULCER_PROPHYLACTICS;

/**
 * VAE Harm Recent SUP Administration implementation
 */
public class RecentStressUlcerProphylaxisAdministration {
  /**
   * VAE Harm Recent SUP Administration
   *
   * @param client
   *     the FHIR client used to query Topaz
   * @param encounterId
   *     the Encounter to search
   * @param startTime
   *     search administrations after this time.
   * @return
   *     True if an administration after the specified time that is in progress or completed
   *     has an order for stress ulcer prophylactics.
   */
  public static boolean recentStressUlcerProphylaxisAdministration(ClientBuilder client,
      String encounterId, Date startTime) {
    List<MedicationAdministration> admins = client.getMedicationAdministrationClient()
        .getMedicationAdministrations(encounterId)
        .stream().filter(admin -> MedicationAdministrationUtils.isAfter(admin, startTime))
        .filter(medicationAdministration -> medicationAdministration.getStatusElement()
            .getValueAsEnum().equals(MedicationAdministrationStatusEnum.IN_PROGRESS)
            || medicationAdministration.getStatusElement().getValueAsEnum()
            .equals(MedicationAdministrationStatusEnum.COMPLETED))
        .filter(medicationAdministration -> medicationAdministration.getReasonNotGiven().isEmpty())
        .collect(Collectors.toList());

    for (MedicationAdministration admin : admins) {
      MedicationOrder order = client.getMedicationOrderClient().read(admin.getPrescription()
          .getReference().getIdPart(), encounterId);
      if (order != null && order.getIdentifierFirstRep().getValue().equals(
          STRESS_ULCER_PROPHYLACTICS.getCode())) {
        return true;
      }
    }

    return false;
  }
}
