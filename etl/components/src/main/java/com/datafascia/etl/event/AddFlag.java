// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.FlagRepository;
import com.datafascia.domain.persist.PatientRepository;
import javax.inject.Inject;

/**
 * Adds flag representing existence of patient care conference note.
 */
public class AddFlag {

  @Inject
  private FlagRepository flagRepository;

  private static UnitedStatesPatient getPatient(String patientIdentifier) {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue(patientIdentifier);
    patient.setId(new IdDt(PatientRepository.generateId(patient).toString()));
    return patient;
  }

  /**
   * Adds flag representing existence of patient care conference note.
   *
   * @param documentType
   *     TXA-2 field value
   * @param activityDateTime
   *     TXA-4 field value
   * @param patientIdentifier
   *     patient identifier
   */
  public void accept(String documentType, DateTimeDt activityDateTime, String patientIdentifier) {
    UnitedStatesPatient patient = getPatient(patientIdentifier);

    FlagBuilder flagBuilder = new FlagBuilder(patient)
        .addDocumentType(documentType, activityDateTime);
    flagBuilder.build()
        .forEach(flag -> flagRepository.save(flag));
  }
}
