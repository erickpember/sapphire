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
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.FlagRepository;
import com.datafascia.domain.persist.ObservationRepository;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.domain.persist.ProcedureRepository;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Adds observations.
 */
@Slf4j
public class AddObservations {

  @Inject
  private Clock clock;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private ObservationRepository observationRepository;

  @Inject
  private ClientBuilder apiClient;

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

    Optional<Encounter> optionalEncounter = encounterRepository.read(Id.of(encounterIdentifier));
    if (!optionalEncounter.isPresent()) {
      log.warn("Encounter identifier [{}] not found. Ignoring observations", encounterIdentifier);
      return;
    }

    Encounter encounter = optionalEncounter.get();
    UnitedStatesPatient patient = getPatient(patientIdentifier);

    FlagBuilder flagBuilder = new FlagBuilder(patient);
    ProcedureBuilder procedureBuilder = new ProcedureBuilder(encounter, apiClient, clock);

    for (Observation observation : observations) {
      observation
          .setSubject(new ResourceReferenceDt(patient))
          .setEncounter(new ResourceReferenceDt(encounter));
      observationRepository.save(encounter, observation);

      flagBuilder.add(observation);
      procedureBuilder.add(observation);
    }

    apiClient.invalidateObservations(encounterIdentifier);

    flagBuilder.build()
        .forEach(flag -> {
          flagRepository.save(flag);
          harmEvidenceUpdater.updateFlag(flag, encounter);
        });

    procedureBuilder.build()
        .ifPresent(procedure -> {
          procedureRepository.save(procedure);
          apiClient.invalidateProcedures(encounterIdentifier);
          harmEvidenceUpdater.updateProcedure(procedure, encounter);
        });

    harmEvidenceUpdater.updateObservations(observations, encounter);
  }
}
