// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
            .setSystem(IdentifierSystems.INSTITUTION_PATIENT_IDENTIFIER).setValue("UCSF-12345");
    patient.addIdentifier()
            .setSystem(IdentifierSystems.ACCOUNT_NUMBER).setValue("12345");
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
            .setSystem(IdentifierSystems.ENCOUNTER_IDENTIFIER).setValue("12345");
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

    Optional<UnitedStatesPatient> patientResult = patientRepository.read(Id.of(encounter.
            getPatient().getReference().getIdPart()));
    assertEquals(patientResult.get().getId().getIdPart(), patient.getId().getIdPart());

    encounterRepository.delete(encounterId);
    encounters = encounterRepository.list(Optional.empty());
    assertEquals(encounters.size(), 0);
  }
}
