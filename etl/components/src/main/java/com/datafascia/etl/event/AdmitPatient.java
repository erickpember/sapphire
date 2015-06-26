// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.PatientRepository;
import java.util.Date;
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
    PeriodDt period = new PeriodDt();
    period.setStart(Date.from(fromEncounter.getAdmitTime()), TemporalPrecisionEnum.SECOND);

    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.ENCOUNTER_IDENTIFIER).setValue(fromEncounter.getIdentifier());
    encounter.setPeriod(period);
    return encounter;
  }

  private static UnitedStatesPatient createPatient(PatientData patientData, Encounter encounter) {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT_IDENTIFIER)
        .setValue(patientData.getInstitutionPatientId());
    patient.addIdentifier()
        .setSystem(IdentifierSystems.ACCOUNT_NUMBER).setValue(patientData.getAccountNumber());
    patient.addName()
        .addGiven(patientData.getFirstName())
        .addGiven(patientData.getMiddleName())
        .addFamily(patientData.getLastName());
    patient.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(patientData.getLanguage()));
    patient
        .setRace(patientData.getRace())
        .setGender(patientData.getGender())
        .setBirthDate(Dates.toDate(patientData.getBirthDate()))
        .setMaritalStatus(patientData.getMaritalStatus())
        .setActive(true);
    return patient;
  }

  @Override
  public void accept(Event event) {
    AdmitPatientData admitPatientData = (AdmitPatientData) event.getData();
    Encounter encounter = createEncounter(admitPatientData.getEncounter());
    UnitedStatesPatient patient = createPatient(admitPatientData.getPatient(), encounter);

    patientRepository.save(patient);
    encounterRepository.save(patient, encounter);
  }
}
