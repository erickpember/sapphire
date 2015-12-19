// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
