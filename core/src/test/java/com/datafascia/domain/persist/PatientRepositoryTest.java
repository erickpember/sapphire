// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.models.Gender;
import com.datafascia.models.MaritalStatus;
import com.datafascia.models.Name;
import com.datafascia.models.Patient;
import com.datafascia.models.Race;
import com.neovisionaries.i18n.LanguageCode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for Patient Repository
 */
public class PatientRepositoryTest extends BaseRepositoryTest {
  private PatientRepository patientRepo;

  @BeforeClass
  public void setupTemplate() {
    patientRepo = new PatientRepository(accumuloTemplate);
  }

  @Test(dependsOnGroups = "patients")
  public void patients() {
    List<Patient> patients = patientRepo.list(Optional.of(false));
    assertEquals(1, patients.size());
    patients.addAll(patientRepo.list(Optional.of(true)));
    assertEquals(3, patients.size());
  }

  @Test
  private void testNonExistentPatient() {
    Optional<Patient> patNonExistent = patientRepo.read(Id.of("asdf"));
    assertEquals(Optional.empty(), patNonExistent);
  }

  @Test(groups = "patients")
  private void getActiveWithLanguages() {
    testPatientSet(new Patient() {
      {
        setId(Id.of("urn:df-patientId-1:96087004"));
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
    });
  }

  @Test(groups = "patients")
  private void getActiveWithoutLanguages() {
    testPatientSet(new Patient() {
      {
        setId(Id.of("urn:df-patientId-1:96087039"));
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
    });
  }

  @Test(groups = "patients")
  private void getInactive() {
    testPatientSet(new Patient() {
      {
        setId(Id.of("urn:df-patientId-1:96087047"));
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
    });
  }

  private void testPatientSet(Patient pat) {
    patientRepo.save(pat);

    // Not checking if the optional has data, as we *want* it to fail if it's empty.
    Patient patfetched = patientRepo.read(pat.getId()).get();

    assertEquals(pat, patfetched);
  }
}
