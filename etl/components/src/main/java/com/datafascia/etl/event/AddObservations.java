// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DecimalDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.domain.event.AddObservationsData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.ObservationRepository;
import com.datafascia.domain.persist.PatientRepository;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Processes add observations event.
 */
public class AddObservations implements Consumer<Event> {

  @Inject
  private transient ObservationRepository observationRepository;

  private static Encounter getEncounter(String encounterIdentifier) {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue(encounterIdentifier);
    encounter.setId(new IdDt(EncounterRepository.generateId(encounter).toString()));
    return encounter;
  }

  private static UnitedStatesPatient getPatient(String patientIdentifier) {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue(patientIdentifier);
    patient.setId(new IdDt(PatientRepository.generateId(patient).toString()));
    return patient;
  }

  private static Observation toObservation(
      ObservationData fromObservation, Encounter encounter, UnitedStatesPatient patient) {

    String observationCode = fromObservation.getId();

    IDatatype observationValue;
    if ("NM".equals(fromObservation.getValueType())) {
      observationValue = new QuantityDt()
          .setValue(new DecimalDt(fromObservation.getValue().get(0)))
          .setUnits(fromObservation.getValueUnits());
    } else {
      observationValue = new StringDt(fromObservation.getValue().get(0));
    }

    Observation observation = new Observation()
        .setCode(new CodeableConceptDt("system", observationCode))
        .setValue(observationValue)
        .setIssued(Dates.toInstant(fromObservation.getObservationDateAndTime()))
        .setEncounter(new ResourceReferenceDt(encounter.getId()))
        .setSubject(new ResourceReferenceDt(patient.getId()));
    return observation;
  }

  @Override
  public void accept(Event event) {
    AddObservationsData addObservationsData = (AddObservationsData) event.getData();
    Encounter encounter = getEncounter(addObservationsData.getEncounterIdentifier());
    UnitedStatesPatient patient = getPatient(addObservationsData.getInstitutionPatientId());

    for (ObservationData fromObservation : addObservationsData.getObservations()) {
      Observation observation = toObservation(fromObservation, encounter, patient);
      observationRepository.save(encounter, observation);
    }
  }
}
