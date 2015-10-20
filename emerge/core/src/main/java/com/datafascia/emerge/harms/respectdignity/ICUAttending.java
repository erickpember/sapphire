// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.respectdignity;

import ca.uhn.fhir.model.dstu2.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.domain.fhir.PractitionerRoleEnum;
import java.util.Date;
import java.util.Optional;

/**
 * Tools for ICU Attending.
 */
public class ICUAttending {

  /**
   * Gets the period start for the most recent ICU attending.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return The start period of the most recent ICU attending.
   */
  public static Date getICUAttendingPeriod(ClientBuilder client, String encounterId) {
    Optional<Participant> participant = PractitionerUtils.getMostRecentPractitionerForRole(
        client.getEncounterClient().getEncounter(encounterId),
        PractitionerRoleEnum.ROLE_ICU_ATTENDING,
        client);
    if (participant.isPresent()) {
      return participant.get().getPeriod().getStart();
    }
    return null;
  }

  /**
   * Gets the name of the most recent ICU attending.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return The name of the most recent ICU attending.
   */
  public static String getICUAttendingName(ClientBuilder client, String encounterId) {
    Optional<Participant> participant = PractitionerUtils.getMostRecentPractitionerForRole(
        client.getEncounterClient().getEncounter(encounterId),
        PractitionerRoleEnum.ROLE_ICU_ATTENDING,
        client);
    if (participant.isPresent()) {
      Practitioner practitioner = client.getPractitionerClient().getPractitioner(
          participant.get().getIndividual().getReference().getIdPart());
      return HumanNames.toFullName(practitioner.getName());
    }
    return null;
  }
}
