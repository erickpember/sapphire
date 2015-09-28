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
   * Gets the most recent practitioner in a given role for an encounter.
   *
   * @param encounter The encounter to check.
   * @param role The role to look for.
   * @param client The client to use.
   * @return The optional name of the practitioner.
   */
  public static Optional<String> getMostRecentPractitionerNameForRole(Encounter encounter,
      PractitionerRoleEnum role, ClientBuilder client) {
    List<Participant> participants = encounter.getParticipant();

    String name = null;
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
            name = HumanNames.toFullName(prac.getName());
          }
        }
      }
    }

    return Optional.of(name);
  }

  /**
   * Gets the start time of the last period of a given role
   *
   * @param encounter The encounter to check.
   * @param role The role to look for.
   * @param client The client to use.
   * @return The start time of the last period of a given role.
   */
  public static Optional<Date> getMostRecentPractitionerStartTimeForRole(Encounter encounter,
      PractitionerRoleEnum role, ClientBuilder client) {
    List<Participant> participants = encounter.getParticipant();

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
          }
        }
      }
    }

    return Optional.of(lastDate);
  }
}
