// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.respectdignity;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.HumanNames;
import com.datafascia.emerge.ucsf.valueset.PractitionerRoleEnum;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Utilities for provider resources.
 */
public class ParticipantFinder {

  /**
   * Found participant data
   */
  @AllArgsConstructor
  @Builder
  @Data
  public static class CareProvider {
    private String name;
    private Date periodStart;
  }

  @Inject
  private ClientBuilder apiClient;

  private Practitioner getPractitioner(Encounter.Participant participant) {
    String practitionerId = participant.getIndividual().getReference().getIdPart();
    return apiClient.getPractitionerClient().getPractitioner(practitionerId);
  }

  /**
   * Finds participant of a specific practitioner role in an encounter.
   *
   * @param encounter
   *     encounter to search
   * @param desiredRole
   *     practitioner role to match
   * @return optional participant, empty if not found
   */
  public Optional<CareProvider> findByPractitionerRole(
      Encounter encounter, PractitionerRoleEnum desiredRole) {

    // Sort into descending order by period start.
    List<Encounter.Participant> sortedParticipants = encounter.getParticipant()
        .stream()
        .sorted((p1, p2) -> p2.getPeriod().getStart().compareTo(p1.getPeriod().getStart()))
        .collect(Collectors.toList());

    // Search sorted list for first participant matching desired role.
    for (Encounter.Participant participant : sortedParticipants) {
      Practitioner practitioner = getPractitioner(participant);
      String roleCode =
          practitioner.getPractitionerRoleFirstRep().getRole().getCodingFirstRep().getCode();
      if (desiredRole.getCode().equals(roleCode)) {
        return Optional.of(
            CareProvider.builder()
                .name(HumanNames.toFullName(practitioner.getName()))
                .periodStart(participant.getPeriod().getStart())
                .build());
      }
    }

    return Optional.empty();
  }
}
