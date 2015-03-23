// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Name;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.Race;
import com.neovisionaries.i18n.LanguageCode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for Patient Repository
 */
public class PatientRepositoryTest extends BaseRepositoryTest {

  private PatientRepository patientRepo;
  private Patient patient1;
  private Patient patient2;
  private Patient patient3;

  @BeforeClass
  public void setupTemplate() {
    patientRepo = new PatientRepository(accumuloTemplate);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_inactive_patients() {
    assertEquals(patientRepo.list(Optional.empty(), Optional.of(false), 1).size(), 1);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_active_patients() {
    assertEquals(patientRepo.list(Optional.empty(), Optional.of(true), 5).size(), 2);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_all_patients() {
    assertEquals(patientRepo.list(Optional.empty(), Optional.empty(), 5).size(), 3);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_patients_starting_from_specified_patient() {
    Id<Patient> startPatientId = PatientRepository.getPatientId(patient2);
    assertEquals(patientRepo.list(Optional.of(startPatientId), Optional.empty(), 5).size(), 2);
  }

  @Test
  private void testNonExistentPatient() {
    Optional<Patient> patNonExistent = patientRepo.read(Id.of("asdf"));
    assertEquals(Optional.empty(), patNonExistent);
  }

  @Test(groups = "patients") @SuppressWarnings("serial")
  private void getActiveWithLanguages() {
    patient1 = new Patient() {
      {
        setInstitutionPatientId("UCSF-12345");
        setAccountNumber("12345");
        setName(new Name() {
          {
            setFirst("pat1firstname");
            setMiddle("pat1middlename");
            setLast("pat1lastname");
          }
        });
        setLangs(new ArrayList<LanguageCode>() {
          {
            add(LanguageCode.en);
            add(LanguageCode.aa);
            add(LanguageCode.tg);
          }
        });
        setGender(Gender.MALE);
        setBirthDate(LocalDate.now());
        setMaritalStatus(MaritalStatus.MARRIED);
        setRace(Race.ASIAN);
        setActive(true);
      }
    };
    testPatientSet(patient1);
  }

  @Test(groups = "patients")
  private void getActiveWithoutLanguages() {
    patient3 = new Patient() {
      {
        setInstitutionPatientId("UCSF-67890");
        setAccountNumber("67890");
        setName(new Name() {
          {
            setFirst("pat2firstname");
            setMiddle("pat2middlename");
            setLast("pat2lastname");
          }
        });
        setLangs(new ArrayList<>());
        setGender(Gender.FEMALE);
        setBirthDate(LocalDate.now());
        setMaritalStatus(MaritalStatus.DIVORCED);
        setRace(Race.PACIFIC_ISLANDER);
        setActive(true);
      }
    };
    testPatientSet(patient3);
  }

  @Test(groups = "patients")
  private void getInactive() {
    patient2 = new Patient() {
      {
        setInstitutionPatientId("UCSF-13579");
        setAccountNumber("13579");
        setName(new Name() {
          {
            setFirst("pat3firstname");
            setMiddle("pat3middlename");
            setLast("pat3lastname");
          }
        });
        setLangs(new ArrayList<>());
        setGender(Gender.UNDIFFERENTIATED);
        setBirthDate(LocalDate.now());
        setMaritalStatus(MaritalStatus.DOMESTIC_PARTNER);
        setRace(Race.BLACK);
        setActive(false);
      }
    };
    testPatientSet(patient2);
  }

  private void testPatientSet(Patient originalPatient) {
    patientRepo.save(originalPatient);

    // Not checking if the optional has data, as we *want* it to fail if it's empty.
    Patient fetchedPatient = patientRepo.read(originalPatient.getId()).get();

    assertEquals(fetchedPatient, originalPatient);
  }
}
