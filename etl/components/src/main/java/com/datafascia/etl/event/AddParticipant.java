// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PractitionerRepository;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Adds participant.
 */
@Slf4j
public class AddParticipant {

  @Inject
  private PractitionerRepository practitionerRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private HarmEvidenceUpdater harmEvidenceUpdater;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Adds participant.
   *
   * @param practitioner
   *     practitioner
   * @param participant
   *     participant
   * @param inputEncounter
   *     encounter
   */
  public void accept(
      Practitioner practitioner, Encounter.Participant participant, Encounter inputEncounter) {

    Id<Encounter> encounterId = EncounterRepository.generateId(inputEncounter);
    Optional<Encounter> optionalEncounter = encounterRepository.read(encounterId);
    if (!optionalEncounter.isPresent()) {
      log.error("encounter ID [{}] not found", encounterId);
      return;
    }
    Encounter encounter = optionalEncounter.get();

    if (Strings.isNullOrEmpty(practitioner.getIdentifierFirstRep().getValue())) {
      log.error("Discarded practitioner with missing identifier for encounter ID {}", encounterId);
      return;
    }

    practitionerRepository.save(practitioner);
    apiClient.invalidatePractitioner(practitioner.getId().getIdPart());

    participant.setIndividual(new ResourceReferenceDt(practitioner));

    // Remove any existing participant for practitioner from the encounter.
    List<Encounter.Participant> participants = encounter.getParticipant()
        .stream()
        .filter(p -> !p.getIndividual().getReference().equals(practitioner.getId()))
        .collect(Collectors.toCollection(() -> new ArrayList<>()));

    // Add the participant to the encounter.
    participants.add(participant);
    encounter.setParticipant(participants);
    encounterRepository.save(encounter);

    apiClient.invalidateEncounter(encounter.getId().getIdPart());

    harmEvidenceUpdater.updateParticipant(practitioner, encounter);
  }
}
