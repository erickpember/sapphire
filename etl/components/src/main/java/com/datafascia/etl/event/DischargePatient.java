// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
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
  private transient HarmEvidenceRepository harmEvidenceRepository;

  /**
   * Discharges patient.
   *
   * @param triggerEvent
   *     HL7 message trigger event
   * @param inputEncounter
   *     encounter
   */
  public void accept(String triggerEvent, Encounter inputEncounter) {
    Id<Encounter> encounterId = EncounterRepository.generateId(inputEncounter);
    Optional<Encounter> optionalEncounter = encounterRepository.read(encounterId);
    if (!optionalEncounter.isPresent()) {
      log.error("encounter ID [{}] not found", encounterId);
      return;
    }

    Encounter encounter = optionalEncounter.get();
    encounter.setStatus(inputEncounter.getStatusElement());
    encounter.getPeriod().setEnd(inputEncounter.getPeriod().getEndElement());
    encounterStatusTransition.updateEncounterStatus(triggerEvent, encounter);

    encounterRepository.save(encounter);

    String patientId = encounter.getPatient().getReference().getIdPart();
    Id<HarmEvidence> recordId = Id.of(patientId);
    harmEvidenceRepository.delete(recordId);
  }
}
