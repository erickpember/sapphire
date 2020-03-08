// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
