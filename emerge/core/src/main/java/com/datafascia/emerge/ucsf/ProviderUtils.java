// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.valueset.PractitionerRoleEnum;
import java.util.List;

/**
 * Utilities for provider resources.
 */
public class ProviderUtils {
  /**
   * Returns the current primary attending for a given encounter.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return The current primary attending.
   */
  public static Encounter.Participant getCurrentPrimaryAttending(ClientBuilder client,
      String encounterId) {
    List<Encounter.Participant> participants
        = client.getEncounterClient().getEncounter(encounterId).getParticipant();

    // Sort by starting period.
    Encounter.Participant[] sortedParticipants
        = (Encounter.Participant[]) participants.stream().sorted((p1, p2)
            -> p1.getPeriod().getStart().compareTo(p2.getPeriod().getStart())).toArray();

    for (Encounter.Participant participant : sortedParticipants) {
      if (participant.getIndividual().getReference().getResourceType().equals("Practitioner")
          && participant.getIndividual().getReference().getValue()
          .equals(PractitionerRoleEnum.ATTENDING_PROVIDER.getCode())) {
        return participant;
      }
    }
    return null;
  }
}
