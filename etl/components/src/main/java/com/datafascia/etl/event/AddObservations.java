// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.FlagRepository;
import com.datafascia.domain.persist.ObservationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.domain.persist.ProcedureRepository;
import com.datafascia.etl.harm.HarmEvidenceUpdater;
import java.time.Clock;
import java.util.List;
import javax.inject.Inject;

/**
 * Adds observations.
 */
public class AddObservations {

  @Inject
  private Clock clock;

  @Inject
  private ObservationRepository observationRepository;

  @Inject
  private FlagRepository flagRepository;

  @Inject
  private ProcedureRepository procedureRepository;

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
   * Adds observations.
   *
   * @param observations
   *     observations to add
   * @param patientIdentifier
   *     patient identifier
   * @param encounterIdentifier
   *     encounter identifier
   */
  public void accept(
      List<Observation> observations, String patientIdentifier, String encounterIdentifier) {

    UnitedStatesPatient patient = getPatient(patientIdentifier);
    Encounter encounter = getEncounter(encounterIdentifier, patient);

    FlagBuilder flagBuilder = new FlagBuilder(patient);
    ProcedureBuilder procedureBuilder = new ProcedureBuilder(encounter, clock);

    for (Observation observation : observations) {
      observation
          .setSubject(new ResourceReferenceDt(patient))
          .setEncounter(new ResourceReferenceDt(encounter));
      observationRepository.save(encounter, observation);

      flagBuilder.add(observation);
      procedureBuilder.add(observation);
    }

    flagBuilder.build()
        .forEach(flag -> {
            flagRepository.save(flag);
            harmEvidenceUpdater.updateFlag(flag, encounter);
          });

    procedureBuilder.build()
        .ifPresent(procedure -> procedureRepository.save(procedure));

    harmEvidenceUpdater.updateObservations(observations, encounter);
  }
}
