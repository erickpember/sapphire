// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.persist.EncounterRepository;
import java.util.Optional;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes discharge patient event.
 */
@Slf4j
public class DischargePatient implements Consumer<Event> {

  @Inject
  private transient EncounterRepository encounterRepository;

  private Id<Encounter> getEncounterId(AdmitPatientData admitPatientData) {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.ENCOUNTER_IDENTIFIER)
        .setValue(admitPatientData.getEncounter().getIdentifier());
    return EncounterRepository.generateId(encounter);
  }

  @Override
  public void accept(Event event) {
    AdmitPatientData admitPatientData = (AdmitPatientData) event.getData();

    Id<Encounter> encounterId = getEncounterId(admitPatientData);
    Optional<Encounter> optionalEncounter = encounterRepository.read(encounterId);
    if (!optionalEncounter.isPresent()) {
      log.error("encounter ID [{}] not found", encounterId);
      return;
    }

    Encounter encounter = optionalEncounter.get();
    encounter.setStatus(EncounterStateEnum.FINISHED);
    encounter.getPeriod().setEnd(
        Dates.toDateTime(admitPatientData.getEncounter().getDischargeTime()));
    encounterRepository.save(encounter);
  }
}
