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

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import com.datafascia.etl.hl7.EncounterStatusTransition;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Discharges patient.
 */
@Slf4j
public class DischargePatient {

  @Inject
  private EncounterStatusTransition encounterStatusTransition;

  @Inject
  private transient EncounterRepository encounterRepository;

  @Inject
  private HarmEvidenceUpdater harmEvidenceUpdater;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Discharges patient.
   *
   * @param triggerEvent
   *     HL7 message trigger event
   * @param newEncounter
   *     encounter
   */
  public void accept(String triggerEvent, Encounter newEncounter) {
    Id<Encounter> encounterId = EncounterRepository.generateId(newEncounter);
    Optional<Encounter> currentEncounter = encounterRepository.read(encounterId);
    if (!currentEncounter.isPresent()) {
      log.error("encounter ID [{}] not found", encounterId);
      return;
    }

    encounterStatusTransition.updateEncounterStatus(triggerEvent, currentEncounter, newEncounter);

    encounterRepository.save(newEncounter);

    apiClient.invalidateEncounter(newEncounter.getId().getIdPart());

    harmEvidenceUpdater.dischargePatient(newEncounter);
  }
}
