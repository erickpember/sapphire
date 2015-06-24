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
import com.datafascia.domain.model.MedicationAdministration;
import com.datafascia.domain.model.MedicationAdministrationDosage;
import com.datafascia.domain.model.MedicationAdministrationStatus;
import com.datafascia.domain.model.NumericQuantity;
import com.datafascia.domain.model.Ratio;
import com.neovisionaries.i18n.LanguageCode;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link MedicationAdministrationRepository} test
 */
public class MedicationAdministrationRepositoryTest extends RepositoryTestSupport {

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private MedicationAdministrationRepository medicationAdministrationRepository;

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

  private MedicationAdministration createMedicationAdministration(
      UnitedStatesPatient patient, Encounter encounter) {

    MedicationAdministration administration = new MedicationAdministration();
    administration.setStatus(MedicationAdministrationStatus.IN_PROGRESS);
    administration.setEffectiveTimePeriod(new Interval<>(Instant.now(), Instant.now()));
    administration.setEncounterId(encounter.getId());
    administration.setMedicationId(Id.of("medicationId"));

    MedicationAdministrationDosage dosage = new MedicationAdministrationDosage();
    dosage.setSite(new CodeableConcept() {
      {
        setCodings(Arrays.asList("site"));
        setText("site");
      }
    });
    dosage.setRoute(new CodeableConcept() {
      {
        setCodings(Arrays.asList("route"));
        setText("route");
      }
    });
    dosage.setMethod(new CodeableConcept() {
      {
        setCodings(Arrays.asList("method"));
        setText("method");
      }
    });
    dosage.setQuantity(new NumericQuantity());
    dosage.setRate(new Ratio(new NumericQuantity(), new NumericQuantity()));
    administration.setDosage(dosage);
    return administration;
  }

  @Test
  public void should_list_medication_administration() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter();
    encounterRepository.save(patient, encounter);

    MedicationAdministration administration = createMedicationAdministration(patient, encounter);
    medicationAdministrationRepository.save(patient, encounter, administration);

    Id<UnitedStatesPatient> patientId = Ids.toPrimaryKey(patient.getId());
    List<MedicationAdministration> administrations =
        medicationAdministrationRepository.list(patientId, encounter.getId());
    assertEquals(administrations.get(0), administration);
  }
}
