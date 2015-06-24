// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.IdentifierSystems;
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

/**
 * Test for Patient Repository
 */
public class PatientRepositoryTest extends RepositoryTestSupport {

  @Inject
  private PatientRepository patientRepository;

  private UnitedStatesPatient patient1;
  private UnitedStatesPatient inactivePatient;
  private UnitedStatesPatient patient3;

  @Test(dependsOnGroups = "patients")
  public void should_list_inactive_patients() {
    List<UnitedStatesPatient> patients = patientRepository.list(
        Optional.empty(), Optional.of(false), 5);

    assertEquals(patients.get(0).getId(), inactivePatient.getId());
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_active_patients() {
    List<UnitedStatesPatient> patients = patientRepository.list(
        Optional.empty(), Optional.of(true), 5);
    assertEquals(patients.size(), 2);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_all_patients() {
    assertEquals(patientRepository.list(Optional.empty(), Optional.empty(), 5).size(), 3);
  }

  @Test(dependsOnGroups = "patients")
  public void should_list_patients_starting_from_specified_patient() {
    Id<UnitedStatesPatient> startPatientId = PatientRepository.generateId(inactivePatient);
    List<UnitedStatesPatient> patients = patientRepository.list(
        Optional.of(startPatientId), Optional.empty(), 5);

    assertEquals(patients.size(), 2);
  }

  @Test
  public void testNonExistentPatient() {
    Optional<UnitedStatesPatient> patNonExistent = patientRepository.read(Id.of("asdf"));
    assertEquals(Optional.empty(), patNonExistent);
  }

  @Test(groups = "patients") @SuppressWarnings("serial")
  public void getActiveWithLanguages() {
    patient1 = new UnitedStatesPatient();
    patient1.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT_IDENTIFIER).setValue("UCSF-12345");
    patient1.addIdentifier()
        .setSystem(IdentifierSystems.ACCOUNT_NUMBER).setValue("12345");
    patient1.addName()
        .addGiven("pat1firstname").addGiven("pat1middlename").addFamily("pat1lastname");
    patient1.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient1
        .setRace(RaceEnum.ASIAN)
        .setMaritalStatus(MaritalStatusCodesEnum.M)
        .setGender(AdministrativeGenderEnum.MALE)
        .setBirthDate(new DateDt(new Date()))
        .setActive(true);

    testPatientSet(patient1);
  }

  @Test(groups = "patients")
  public void getActiveWithoutLanguages() {
    patient3 = new UnitedStatesPatient();
    patient3.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT_IDENTIFIER).setValue("UCSF-67890");
    patient3.addIdentifier()
        .setSystem(IdentifierSystems.ACCOUNT_NUMBER).setValue("67890");
    patient3.addName()
        .addGiven("pat3firstname").addGiven("pat3middlename").addFamily("pat3lastname");
    patient3.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    patient3
        .setRace(RaceEnum.PACIFIC_ISLANDER)
        .setMaritalStatus(MaritalStatusCodesEnum.D)
        .setGender(AdministrativeGenderEnum.FEMALE)
        .setBirthDate(new DateDt(new Date()))
        .setActive(true);

    testPatientSet(patient3);
  }

  @Test(groups = "patients")
  public void getInactive() {
    inactivePatient = new UnitedStatesPatient();
    inactivePatient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT_IDENTIFIER).setValue("UCSF-13579");
    inactivePatient.addIdentifier()
        .setSystem(IdentifierSystems.ACCOUNT_NUMBER).setValue("13579");
    inactivePatient.addName()
        .addGiven("pat2firstname").addGiven("pat2middlename").addFamily("pat2lastname");
    inactivePatient.addCommunication()
        .setPreferred(true).setLanguage(Languages.createLanguage(LanguageCode.en));
    inactivePatient
        .setRace(RaceEnum.BLACK)
        .setMaritalStatus(MaritalStatusCodesEnum.P)
        .setGender(AdministrativeGenderEnum.UNKNOWN)
        .setBirthDate(new DateDt(new Date()))
        .setActive(false);

    testPatientSet(inactivePatient);
  }

  private void testPatientSet(UnitedStatesPatient originalPatient) {
    patientRepository.save(originalPatient);

    Id<UnitedStatesPatient> patientId = Id.of(originalPatient.getId().getIdPart());
    UnitedStatesPatient fetchedPatient = patientRepository.read(patientId).get();

    assertEquals(fetchedPatient.getId(), originalPatient.getId());
  }
}
