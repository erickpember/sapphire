// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
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

    harmEvidenceUpdater.dischargePatient(newEncounter);
  }
}
