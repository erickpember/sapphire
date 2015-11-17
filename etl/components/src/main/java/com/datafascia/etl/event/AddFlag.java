// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.FlagRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import javax.inject.Inject;

/**
 * Adds flag representing existence of patient care conference note.
 */
public class AddFlag {

  @Inject
  private FlagRepository flagRepository;

  @Inject
  private HarmEvidenceUpdater harmEvidenceUpdater;

  private static UnitedStatesPatient getPatient(String patientIdentifier) {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue(patientIdentifier);
    patient.setId(new IdDt(PatientRepository.generateId(patient).toString()));
    return patient;
  }

  private static Encounter getEncounter(String encounterIdentifier, UnitedStatesPatient patient) {
    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue(encounterIdentifier);
    encounter.setId(new IdDt(EncounterRepository.generateId(encounter).toString()));

    encounter.setPatient(new ResourceReferenceDt(patient));
    return encounter;
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
   * @param encounterIdentifier
   *     encounter identifier
   */
  public void accept(
      String documentType,
      DateTimeDt activityDateTime,
      String patientIdentifier,
      String encounterIdentifier) {

    UnitedStatesPatient patient = getPatient(patientIdentifier);
    Encounter encounter = getEncounter(encounterIdentifier, patient);

    FlagBuilder flagBuilder = new FlagBuilder(patient)
        .addDocumentType(documentType, activityDateTime);
    flagBuilder.build()
        .forEach(flag -> {
          flagRepository.save(flag);
          harmEvidenceUpdater.updateFlag(flag, encounter);
        });
  }
}
