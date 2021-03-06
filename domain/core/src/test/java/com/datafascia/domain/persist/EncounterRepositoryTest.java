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
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.neovisionaries.i18n.LanguageCode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * {@link EncounterRepository} test
 */
public class EncounterRepositoryTest extends RepositoryTestSupport {

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue("UCSF-12345");
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("12345");
    patient.addName()
        .addGiven("pat1firstname").addGiven("pat1middlename").addFamily("pat1lastname");
    patient.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient
        .setRace(RaceEnum.ASIAN)
        .setMaritalStatus(MaritalStatusCodesEnum.M)
        .setGender(AdministrativeGenderEnum.MALE)
        .setBirthDate(new DateDt(new Date()))
        .setActive(true);
    return patient;
  }

  private Encounter createEncounter(UnitedStatesPatient patient) {
    PeriodDt period = new PeriodDt();
    period.setStart(new Date(), TemporalPrecisionEnum.DAY);

    Encounter encounter = new Encounter();
    encounter.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("12345");
    encounter
        .setPeriod(period)
        .setStatus(EncounterStateEnum.IN_PROGRESS)
        .setPatient(new ResourceReferenceDt(patient.getId()));
    return encounter;
  }

  @Test
  public void list_read_delete_encounters() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter(patient);
    encounterRepository.save(encounter);

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    List<Encounter> encounters = encounterRepository.list(Optional.empty());
    assertEquals(encounters.size(), 1);

    encounters = encounterRepository.list(Optional.of(EncounterStateEnum.IN_PROGRESS));
    assertEquals(encounters.size(), 1);

    encounters = encounterRepository.list(Optional.of(EncounterStateEnum.ARRIVED));

    Optional<Encounter> result = encounterRepository.read(encounterId);
    assertTrue(result.isPresent());
    assertEquals(result.get().getId().getValue(), encounter.getId().getValue());

    Optional<UnitedStatesPatient> patientResult = patientRepository.read(
        Id.of(encounter.getPatient().getReference().getIdPart()));
    assertEquals(patientResult.get().getId().getIdPart(), patient.getId().getIdPart());

    encounterRepository.delete(encounter);
    encounters = encounterRepository.list(Optional.empty());
    assertEquals(encounters.size(), 0);
  }

  @Test
  public void status_change_should_update_status_index() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter(patient);
    encounterRepository.save(encounter);

    List<Encounter> encounters =
        encounterRepository.list(Optional.of(EncounterStateEnum.IN_PROGRESS));
    assertEquals(encounters.size(), 1);

    encounter.setStatus(EncounterStateEnum.FINISHED);
    encounterRepository.save(encounter);

    encounters = encounterRepository.list(Optional.of(EncounterStateEnum.IN_PROGRESS));
    assertEquals(encounters.size(), 0);
  }

  @Test
  public void delete_encounter_should_update_status_index() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter(patient);
    encounterRepository.save(encounter);

    List<Encounter> encounters =
        encounterRepository.list(Optional.of(EncounterStateEnum.IN_PROGRESS));
    assertEquals(encounters.size(), 1);

    encounterRepository.delete(encounter);

    encounters = encounterRepository.list(Optional.of(EncounterStateEnum.IN_PROGRESS));
    assertEquals(encounters.size(), 0);
  }
}
