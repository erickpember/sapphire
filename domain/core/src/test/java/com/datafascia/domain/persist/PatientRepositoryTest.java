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
    patient1 = new Patient() {
      {
        setInstitutionPatientId("UCSF-12345");
        setAccountNumber("12345");
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("pat1firstname");
            setMiddleName("pat1middlename");
            setFamily(Arrays.asList("pat1lastname"));
          }
        }));
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
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
  public void getActiveWithoutLanguages() {
    patient3 = new Patient() {
      {
        setInstitutionPatientId("UCSF-67890");
        setAccountNumber("67890");
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("pat2firstname");
            setMiddleName("pat2middlename");
            setFamily(Arrays.asList("pat2lastname"));
          }
        }));
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
          }
        });
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
  public void getInactive() {
    inactivePatient = new Patient() {
      {
        setInstitutionPatientId("UCSF-13579");
        setAccountNumber("13579");
        setNames(Arrays.asList(new HumanName() {
          {
            setFirstName("pat3firstname");
            setMiddleName("pat3middlename");
            setFamily(Arrays.asList("pat3lastname"));
          }
        }));
        setCommunication(new PatientCommunication() {
          {
            setPreferred(true);
            setLanguage(new CodeableConcept() {
              {
                setCodings(Arrays.asList("EN"));
                setText("EN");
              }
            });
          }
        });
        setGender(Gender.UNKNOWN);
        setBirthDate(LocalDate.now());
        setMaritalStatus(MaritalStatus.DOMESTIC_PARTNER);
        setRace(Race.BLACK);
        setActive(false);
      }
    };
    testPatientSet(inactivePatient);
  }

  private void testPatientSet(Patient originalPatient) {
    patientRepository.save(originalPatient);

    // Not checking if the optional has data, as we *want* it to fail if it's empty.
    Patient fetchedPatient = patientRepository.read(originalPatient.getId()).get();

    assertEquals(fetchedPatient, originalPatient);
  }
}
