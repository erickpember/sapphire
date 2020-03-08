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
package com.datafascia.etl.hl7;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

/**
 * Computes next encounter status.
 */
public class EncounterStatusTransition {

  /**
   * Fact passed to rules engine.
   */
  @AllArgsConstructor
  @Data
  public static class MessageType {
    private String triggerEvent;
  }

  private KieContainer container;

  /**
   * Constructor
   */
  public EncounterStatusTransition() {
    KieServices services = KieServices.Factory.get();
    container = services.newKieClasspathContainer();
  }

  /**
   * Computes next encounter status.
   *
   * @param triggerEvent
   *     HL7 message trigger event
   * @param currentEncounter
   *     current encounter
   * @param newEncounter
   *     new encounter
   */
  public void updateEncounterStatus(
      String triggerEvent, Optional<Encounter> currentEncounter, Encounter newEncounter) {

    EncounterStateEnum currentStatus = currentEncounter
        .map(encounter -> encounter.getStatusElement().getValueAsEnum())
        .orElse(null);
    newEncounter.setStatus(currentStatus);

    StatelessKieSession session = container.newStatelessKieSession("encounterStatus");
    session.execute(Arrays.asList(new MessageType(triggerEvent), newEncounter));

    newEncounter.setStatus(MoreObjects.firstNonNull(
        newEncounter.getStatusElement().getValueAsEnum(),
        EncounterStateEnum.IN_PROGRESS));
  }
}
