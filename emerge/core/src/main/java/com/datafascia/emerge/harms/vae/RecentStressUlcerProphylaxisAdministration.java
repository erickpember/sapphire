// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

import static com.datafascia.emerge.ucsf.codes.MedicationOrderEnum.STRESS_ULCER_PROPHYLACTICS;

/**
 * Checks if there is a recent stress ulcer prophylactics administration.
 */
public class RecentStressUlcerProphylaxisAdministration {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if there is a recent stress ulcer prophylactics administration.
   *
   * @param encounterId
   *     encounter to search
   * @return true if there is a recent stress ulcer prophylactics administration
   */
  public boolean test(String encounterId) {
    Instant now = Instant.now(clock);
    Date effectiveLowerBound = Date.from(now.minus(25, ChronoUnit.HOURS));

    List<MedicationAdministration> admins = apiClient.getMedicationAdministrationClient()
        .search(encounterId);
    admins = MedicationAdministrationUtils.freshestOfAllOrders(admins).values()
        .stream()
        .filter(administration ->
            MedicationAdministrationUtils.isAfter(administration, effectiveLowerBound))
        .filter(
            administration ->
                administration.getStatusElement().getValueAsEnum() ==
                    MedicationAdministrationStatusEnum.IN_PROGRESS ||
                administration.getStatusElement().getValueAsEnum() ==
                    MedicationAdministrationStatusEnum.COMPLETED)
        .filter(administration -> administration.getReasonNotGiven().isEmpty())
        .collect(Collectors.toList());

    for (MedicationAdministration admin : admins) {
      if (admin != null && MedicationAdministrationUtils.findIdentifiers(admin,
          CodingSystems.UCSF_MEDICATION_GROUP_NAME).stream().anyMatch(id -> id.getValue().equals(
                  STRESS_ULCER_PROPHYLACTICS.getCode()))) {
        return true;
      }
    }

    return false;
  }
}
