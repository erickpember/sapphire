// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import com.datafascia.common.time.Interval;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.HumanName;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.PatientCommunication;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PatientRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Processes admin patient event.
 */
public class AdmitPatient implements Consumer<Event> {

  @Inject
  private transient PatientRepository patientRepository;

  @Inject
  private transient EncounterRepository encounterRepository;

  private static Encounter createEncounter(EncounterData fromEncounter) {
    Interval<Instant> period = new Interval<>();
    period.setStartInclusive(fromEncounter.getAdmitTime());

    Encounter encounter = new Encounter();
    encounter.setIdentifier(fromEncounter.getIdentifier());
    encounter.setId(EncounterRepository.generateId(encounter));
    encounter.setPeriod(period);
    return encounter;
  }

  private static Patient createPatient(PatientData patientData, Encounter encounter) {
    Patient patient = new Patient();
    patient.setInstitutionPatientId(patientData.getInstitutionPatientId());
    patient.setAccountNumber(patientData.getAccountNumber());
    patient.setNames(Arrays.asList(new HumanName()));
    patient.getNames().get(0).setFirstName(patientData.getFirstName());
    patient.getNames().get(0).setMiddleName(patientData.getMiddleName());
    patient.getNames().get(0).setLastName(patientData.getLastName());
    patient.setGender(patientData.getGender());
    patient.setBirthDate(patientData.getBirthDate());
    patient.setMaritalStatus(patientData.getMaritalStatus());
    patient.setRace(patientData.getRace());
    patient.setCommunication(new PatientCommunication(new CodeableConcept(Arrays.
        asList(patientData.getLanguage().toString()), patientData.getLanguage().toString()),true));
    patient.setLastEncounterId(encounter.getId());
    patient.setActive(true);
    return patient;
  }

  @Override
  public void accept(Event event) {
    AdmitPatientData admitPatientData = (AdmitPatientData) event.getData();
    Encounter encounter = createEncounter(admitPatientData.getEncounter());
    Patient patient = createPatient(admitPatientData.getPatient(), encounter);

    patientRepository.save(patient);
    encounterRepository.save(patient, encounter);
  }
}
