// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.Optional;
import javax.inject.Inject;
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

  @Inject
  private EncounterRepository encounterRepository;

  private StatelessKieSession session;

  /**
   * Constructor
   */
  public EncounterStatusTransition() {
    KieServices services = KieServices.Factory.get();
    KieContainer container = services.getKieClasspathContainer();
    session = container.newStatelessKieSession("encounterStatus");
  }

  private EncounterStateEnum readCurrentStatus(Encounter encounter) {
    Id<Encounter> encounterId = EncounterRepository.generateId(encounter);
    Optional<Encounter> optionalEncounter = encounterRepository.read(encounterId);
    if (optionalEncounter.isPresent()) {
      return optionalEncounter.get().getStatusElement().getValueAsEnum();
    }
    return null;
  }

  /**
   * Computes next encounter status.
   *
   * @param triggerEvent
   *     HL7 message trigger event
   * @param encounter
   *     to update
   */
  public void updateEncounterStatus(String triggerEvent, Encounter encounter) {
    encounter.setStatus(readCurrentStatus(encounter));

    session.execute(Arrays.asList(new MessageType(triggerEvent), encounter));

    encounter.setStatus(MoreObjects.firstNonNull(
        encounter.getStatusElement().getValueAsEnum(),
        EncounterStateEnum.IN_PROGRESS));
  }
}
