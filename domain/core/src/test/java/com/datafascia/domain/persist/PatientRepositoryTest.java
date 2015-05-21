// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.HumanName;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.PatientCommunication;
import com.datafascia.domain.model.Race;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for Patient Repository
 */
public class PatientRepositoryTest extends RepositoryTestSupport {

  @Inject
  private PatientRepository patientRepository;

  private Patient patient1;
  private Patient inactivePatient;
  private Patient patient3;

  @Test(dependsOnGroups = "patients")
  public void should_list_inactive_patients() {
    List<Patient> patients = patientRepository.list(Optional.empty(), Optional.of(false), 5);

    assertEquals(patients.get(0), inactivePatient);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_active_patients() {
    assertEquals(patientRepository.list(Optional.empty(), Optional.of(true), 5).size(), 2);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_all_patients() {
    assertEquals(patientRepository.list(Optional.empty(), Optional.empty(), 5).size(), 3);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_patients_starting_from_specified_patient() {
    Id<Patient> startPatientId = PatientRepository.generateId(inactivePatient);
    List<Patient> patients = patientRepository.list(
        Optional.of(startPatientId), Optional.empty(), 5);

    assertEquals(patients.size(), 2);
  }

  @Test
  public void testNonExistentPatient() {
    Optional<Patient> patNonExistent = patientRepository.read(Id.of("asdf"));
    assertEquals(Optional.empty(), patNonExistent);
  }

  @Test(groups = "patients") @SuppressWarnings("serial")
  public void getActiveWithLanguages() {
    patient1 = Patient.builder()
        .institutionPatientId("UCSF-12345")
        .accountNumber("12345")
        .maritalStatus(MaritalStatus.MARRIED)
        .race(Race.ASIAN)
        .active(true)
        .build();
    patient1.setNames(Arrays.asList(HumanName.builder()
        .given(Arrays.asList("pat1firstname", "pat1middlename"))
        .family(Arrays.asList("pat1lastname"))
        .build()));
    patient1.setCommunication(PatientCommunication.builder()
        .preferred(true)
        .language(new CodeableConcept("en", "English"))
        .build());
    patient1.setGender(Gender.MALE);
    patient1.setBirthDate(LocalDate.now());

    testPatientSet(patient1);
  }

  @Test(groups = "patients")
  public void getActiveWithoutLanguages() {
    patient3 = Patient.builder()
        .institutionPatientId("UCSF-67890")
        .accountNumber("67890")
        .maritalStatus(MaritalStatus.DIVORCED)
        .race(Race.PACIFIC_ISLANDER)
        .active(true)
        .build();
    patient3.setNames(Arrays.asList(HumanName.builder()
        .given(Arrays.asList("pat3firstname", "pat3middlename"))
        .family(Arrays.asList("pat2lastname"))
        .build()));
    patient3.setCommunication(PatientCommunication.builder()
        .preferred(true)
        .language(new CodeableConcept("en", "English"))
        .build());
    patient3.setGender(Gender.FEMALE);
    patient3.setBirthDate(LocalDate.now());

    testPatientSet(patient3);
  }

  @Test(groups = "patients")
  public void getInactive() {
    inactivePatient = Patient.builder()
        .institutionPatientId("UCSF-13579")
        .accountNumber("13579")
        .maritalStatus(MaritalStatus.DOMESTIC_PARTNER)
        .race(Race.BLACK)
        .active(false)
        .build();
    inactivePatient.setNames(Arrays.asList(HumanName.builder()
        .given(Arrays.asList("pat2firstname", "pat2middlename"))
        .family(Arrays.asList("pat2lastname"))
        .build()));
    inactivePatient.setCommunication(PatientCommunication.builder()
        .preferred(true)
        .language(new CodeableConcept("en", "English"))
        .build());
    inactivePatient.setGender(Gender.UNKNOWN);
    inactivePatient.setBirthDate(LocalDate.now());

    testPatientSet(inactivePatient);
  }

  private void testPatientSet(Patient originalPatient) {
    patientRepository.save(originalPatient);

    // Not checking if the optional has data, as we *want* it to fail if it's empty.
    Patient fetchedPatient = patientRepository.read(originalPatient.getId()).get();

    assertEquals(fetchedPatient, originalPatient);
  }
}
