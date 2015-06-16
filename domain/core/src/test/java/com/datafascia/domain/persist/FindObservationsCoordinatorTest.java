// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.time.Interval;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.HumanName;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Observation;
import com.datafascia.domain.model.ObservationValue;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.PatientCommunication;
import com.datafascia.domain.model.Race;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
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

  private Patient createPatient() {
    Patient patient = new Patient();
    patient.setInstitutionPatientId("UCSF-12345");
    patient.setAccountNumber("12345");
    patient.setNames(Arrays.asList(HumanName.builder()
        .given(Arrays.asList("pat1firstname", "pat1middlename"))
        .family(Arrays.asList("pat1lastname"))
        .build()));
    patient.setCommunication(PatientCommunication.builder()
        .preferred(true)
        .language(new CodeableConcept("en", "English"))
        .build());
    patient.setGender(Gender.MALE);
    patient.setBirthDate(LocalDate.now());
    patient.setMaritalStatus(MaritalStatus.MARRIED);
    patient.setRace(Race.ASIAN);
    patient.setActive(true);
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
    Patient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter();
    encounterRepository.save(patient, encounter);

    Observation observation1 = createObservation(NUMERICAL_PAIN_LEVEL_LOW, "1");
    observationRepository.save(patient, encounter, observation1);

    Observation observation2 = createObservation(NUMERICAL_PAIN_LEVEL_HIGH, "2");
    observationRepository.save(patient, encounter, observation2);

    FindObservationsCoordinator findObservationsCoordinator = new FindObservationsCoordinator(
        encounterRepository, observationRepository);

    List<Observation> observations =
        findObservationsCoordinator.findObservationsByPatientId(patient.getId(), Optional.empty());
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
