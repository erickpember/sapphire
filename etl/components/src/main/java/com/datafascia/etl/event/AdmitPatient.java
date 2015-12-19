// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import ca.uhn.fhir.model.dstu2.valueset.EncounterLocationStatusEnum;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.LocationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import com.datafascia.etl.hl7.EncounterStatusTransition;
import com.google.common.base.Strings;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Admits, transfers, updates patient.
 */
@Slf4j
public class AdmitPatient {

  @Inject
  private EncounterStatusTransition encounterStatusTransition;

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private LocationRepository locationRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private HarmEvidenceUpdater harmEvidenceUpdater;

  /**
   * Checks if trigger event is admit or transfer.
   *
   * @param triggerEvent
   *     trigger event
   * @return true if trigger event is admit or transfer
   */
  public static boolean isAdmitOrTransfer(String triggerEvent) {
    return "A01".equals(triggerEvent) || "A02".equals(triggerEvent);
  }

  /**
   * Admits patient.
   *
   * @param triggerEvent
   *     HL7 message trigger event
   * @param patient
   *     patient
   * @param location
   *     location
   * @param newEncounter
   *     encounter
   */
  public void accept(
      String triggerEvent,
      UnitedStatesPatient patient,
      Location location,
      Encounter newEncounter) {

    patientRepository.save(patient);

    if (Strings.isNullOrEmpty(location.getIdentifierFirstRep().getValue())) {
      log.error("Discarded location with missing identifier for encounter ID {}, trigger event {}",
          newEncounter.getIdentifierFirstRep().getValue(), triggerEvent);
    } else {
      locationRepository.save(location);
    }

    newEncounter
        .setPatient(new ResourceReferenceDt(patient))
        .addLocation()
            .setLocation(new ResourceReferenceDt(location))
            .setStatus(EncounterLocationStatusEnum.PRESENT);

    Id<Encounter> encounterId = EncounterRepository.generateId(newEncounter);
    Optional<Encounter> currentEncounter = encounterRepository.read(encounterId);

    encounterStatusTransition.updateEncounterStatus(triggerEvent, currentEncounter, newEncounter);

    if (currentEncounter.isPresent()) {
      if (!"A02".equals(triggerEvent)) {
        // If not a transfer, then preserve current period.
        newEncounter.setPeriod(currentEncounter.get().getPeriod());
      }
    }

    encounterRepository.save(newEncounter);

    if (isAdmitOrTransfer(triggerEvent)) {
      harmEvidenceUpdater.admitPatient(patient, location, newEncounter);
    } else {
      harmEvidenceUpdater.updatePatient(patient, location, newEncounter);
    }
  }
}
