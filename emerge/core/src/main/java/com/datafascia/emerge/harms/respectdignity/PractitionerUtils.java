// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.respectdignity;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.domain.fhir.PractitionerRoleEnum;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Utilities for getting practitioner data.
 */
public class PractitionerUtils {

  /**
   * Gets the name for a given practitioner.
   *
   * @param prac The practitioner from which to fetch.
   * @return The optional name of the practitioner.
   */
  public static Optional<String> getNameForPractitioner(Practitioner prac) {
    return Optional.of(HumanNames.toFullName(prac.getName()));
  }

  /**
   * Gets the start time a given participant.
   *
   * @param par to Participant from which to fetch.
   * @return The start time of the participant.
   */
  public static Optional<Date> getStartTimeForParticipant(Participant par) {
    return Optional.of(par.getPeriod().getStart());
  }

  /**
   * Gets the most recent practitioner for a given role.
   * @param encounter The encounter to query with.
   * @param role The role to check.
   * @param client The client to use.
   * @return The most recent practitioner for the role.
   */
  public static Optional<Participant> getMostRecentPractitionerForRole(Encounter encounter,
      PractitionerRoleEnum role, ClientBuilder client) {
    List<Participant> participants = encounter.getParticipant();

    Participant lastPractitioner = null;
    Date lastDate = null;
    for (Participant par : participants) {
      if (par.getIndividual().getReference().getResourceType()
          .equals(Practitioner.class.getSimpleName())) {
        Practitioner prac
            = client.getPractitionerClient().getPractitioner(
                par.getIndividual().getReference().getIdPart());
        if (prac.getIdentifierFirstRep().getValue().equals(role.getCode())) {
          if (lastDate == null || par.getPeriod().getStart().after(lastDate)) {
            lastDate = par.getPeriod().getStart();
            lastPractitioner = par;
          }
        }
      }
    }

    return Optional.of(lastPractitioner);
  }
}
