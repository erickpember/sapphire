// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import com.datafascia.domain.event.AddObservationsData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Observation;
import com.datafascia.domain.model.ObservationValue;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.ObservationRepository;
import com.datafascia.domain.persist.PatientRepository;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Processes add observations event.
 */
public class AddObservations implements Consumer<Event> {

  @Inject
  private transient ObservationRepository observationRepository;

  private static Patient getPatient(String institutionPatientId) {
    Patient patient = new Patient();
    patient.setInstitutionPatientId(institutionPatientId);
    patient.setId(PatientRepository.generateId(patient));
    return patient;
  }

  private static Encounter getEncounter(String encounterIdentifier) {
    Encounter encounter = new Encounter();
    encounter.setIdentifier(encounterIdentifier);
    encounter.setId(EncounterRepository.generateId(encounter));
    return encounter;
  }

  private static Observation toObservation(ObservationData fromObservation) {
    ObservationValue observationValue = new ObservationValue();
    observationValue.setString(fromObservation.getValue().get(0));

    String code = fromObservation.getId();
    Observation observation = new Observation();
    observation.setName(new CodeableConcept(Arrays.asList(code), code));
    observation.setValue(observationValue);
    observation.setIssued(fromObservation.getObservationDateAndTime());
    return observation;
  }

  @Override
  public void accept(Event event) {
    AddObservationsData addObservationsData = (AddObservationsData) event.getData();
    Patient patient = getPatient(addObservationsData.getInstitutionPatientId());
    Encounter encounter = getEncounter(addObservationsData.getEncounterIdentifier());

    for (ObservationData fromObservation : addObservationsData.getObservations()) {
      Observation observation = toObservation(fromObservation);
      observationRepository.save(patient, encounter, observation);
    }
  }
}
