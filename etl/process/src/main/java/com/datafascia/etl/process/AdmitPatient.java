// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import com.datafascia.common.persist.Id;
import com.datafascia.domain.event.AdmitData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.models.Name;
import com.datafascia.models.Patient;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Processes admin patient event.
 */
public class AdmitPatient implements Consumer<Event> {

  @Inject
  private transient PatientRepository patientRepository;

  @Override
  public void accept(Event event) {
    AdmitData admitData = (AdmitData) event.getData();
    PatientData patientData = admitData.getPatient();

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
    patient.setActive(true);

    patientRepository.save(patient);
  }
}
