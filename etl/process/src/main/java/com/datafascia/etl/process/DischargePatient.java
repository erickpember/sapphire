// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import com.datafascia.common.persist.Id;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.persist.PatientRepository;
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
  private transient PatientRepository patientRepository;

  private Id<Patient> getPatientId(AdmitPatientData admitPatientData) {
    Patient patient = new Patient();
    patient.setInstitutionPatientId(admitPatientData.getPatient().getInstitutionPatientId());
    return PatientRepository.getEntityId(patient);
  }

  @Override
  public void accept(Event event) {
    AdmitPatientData admitPatientData = (AdmitPatientData) event.getData();

    Id<Patient> patientId = getPatientId(admitPatientData);
    Optional<Patient> optionalPatient = patientRepository.read(patientId);
    if (optionalPatient.isPresent()) {
      Patient patient = optionalPatient.get();
      patient.setActive(false);
      patientRepository.save(patient);
    } else {
      log.error("patient ID [{}] not found", patientId);
    }
  }
}
