// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Observation;
import com.datafascia.domain.model.ObservationValue;
import com.neovisionaries.i18n.LanguageCode;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link FindObservationsCoordinator} test
 */
public class FindObservationsCoordinatorTest extends RepositoryTestSupport {

  private static final String NUMERICAL_PAIN_LEVEL_LOW = "numericalPainLevelLow";
  private static final String NUMERICAL_PAIN_LEVEL_HIGH = "numericalPainLevelHigh";

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private ObservationRepository observationRepository;

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

  private Encounter createEncounter() {
    Interval<Instant> period = new Interval<>();
    period.setStartInclusive(Instant.now());

    Encounter encounter = new Encounter();
    encounter.setIdentifier("encounterIdentifier");
    encounter.setPeriod(period);
    return encounter;
  }

  private Observation createObservation(String code, String value) {
    ObservationValue observationValue = new ObservationValue();
    observationValue.setString(value);

    Observation observation = new Observation();
    observation.setCode(new CodeableConcept(Arrays.asList(code), code));
    observation.setValue(observationValue);
    observation.setIssued(Instant.now());
    return observation;
  }

  @Test
  public void should_list_observations() throws Exception {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter();
    encounterRepository.save(patient, encounter);

    Observation observation1 = createObservation(NUMERICAL_PAIN_LEVEL_LOW, "1");
    observationRepository.save(patient, encounter, observation1);

    Observation observation2 = createObservation(NUMERICAL_PAIN_LEVEL_HIGH, "2");
    observationRepository.save(patient, encounter, observation2);

    FindObservationsCoordinator findObservationsCoordinator = new FindObservationsCoordinator(
        encounterRepository, observationRepository);

    Id<UnitedStatesPatient> patientId = Ids.toPrimaryKey(patient.getId());
    List<Observation> observations =
        findObservationsCoordinator.findObservationsByPatientId(patientId, Optional.empty());
    assertEquals(observations.size(), 2);
    for (Observation observation : observations) {
      switch (observation.getCode().getCodings().get(0)) {
        case NUMERICAL_PAIN_LEVEL_LOW:
          assertEquals(observation, observation1);
          break;
        case NUMERICAL_PAIN_LEVEL_HIGH:
          assertEquals(observation, observation2);
          break;
      }
    }
  }
}
