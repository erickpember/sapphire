// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Location;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.LocationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.etl.harm.HarmEvidenceUpdater;
import com.datafascia.etl.hl7.EncounterStatusTransition;
import javax.inject.Inject;

/**
 * Admits patient.
 */
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
   * Admits patient.
   *
   * @param triggerEvent
   *     HL7 message trigger event
   * @param patient
   *     patient
   * @param location
   *     location
   * @param encounter
   *     encounter
   */
  public void accept(
      String triggerEvent,
      UnitedStatesPatient patient,
      Location location,
      Encounter encounter) {

    patientRepository.save(patient);

    locationRepository.save(location);

    encounter
        .setPatient(new ResourceReferenceDt(patient))
        .addLocation().setLocation(new ResourceReferenceDt(location));
    encounterStatusTransition.updateEncounterStatus(triggerEvent, encounter);

    encounterRepository.save(encounter);

    harmEvidenceUpdater.admitPatient(patient, location, encounter);
  }
}
