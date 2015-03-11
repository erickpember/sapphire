// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.domain.event.AdmitData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Hospitalization;
import com.datafascia.domain.model.Name;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PatientRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
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

    Hospitalization hospitalization = new Hospitalization();
    hospitalization.setPeriod(period);

    Encounter encounter = new Encounter();
    encounter.setId(Id.of(UUID.randomUUID().toString()));
    encounter.setHospitalisation(hospitalization);
    return encounter;
  }

  private Patient createPatient(PatientData patientData, Encounter encounter) {
    Patient patient = new Patient();
    patient.setId(Id.of(patientData.getInstitutionPatientId()));
    patient.setInstitutionPatientId(patientData.getInstitutionPatientId());
    patient.setAccountNumber(patientData.getAccountNumber());
    patient.setName(new Name());
    patient.getName().setFirst(patientData.getFirstName());
    patient.getName().setMiddle(patientData.getMiddleName());
    patient.getName().setLast(patientData.getLastName());
    patient.setGender(patientData.getGender());
    patient.setBirthDate(patientData.getBirthDate());
    patient.setMaritalStatus(patientData.getMaritalStatus());
    patient.setRace(patientData.getRace());
    patient.setLangs(Arrays.asList(patientData.getLanguage()));
    patient.setLastEncounterId(encounter.getId());
    patient.setActive(true);
    return patient;
  }

  @Override
  public void accept(Event event) {
    AdmitData admitData = (AdmitData) event.getData();
    Encounter encounter = createEncounter(admitData.getEncounter());
    Patient patient = createPatient(admitData.getPatient(), encounter);

    patientRepository.save(patient);
    encounterRepository.save(patient, encounter);
  }
}
