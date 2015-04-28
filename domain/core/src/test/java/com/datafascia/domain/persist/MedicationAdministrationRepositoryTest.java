// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.domain.model.CodeableConcept;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.HumanName;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.MedicationAdministration;
import com.datafascia.domain.model.MedicationAdministrationDosage;
import com.datafascia.domain.model.MedicationAdministrationStatus;
import com.datafascia.domain.model.NumericQuantity;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.model.PatientCommunication;
import com.datafascia.domain.model.Race;
import com.datafascia.domain.model.Ratio;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
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

  private Patient createPatient() {
    Patient patient = new Patient();
    patient.setInstitutionPatientId("UCSF-12345");
    patient.setAccountNumber("12345");
    patient.setNames(Arrays.asList(new HumanName() {
      {
        setFirstName("pat1firstname");
        setMiddleName("pat1middlename");
        setFamily(Arrays.asList("pat1lastname"));
      }
    }));
    patient.setCommunication(new PatientCommunication() {
      {
        setPreferred(true);
        setLanguage(new CodeableConcept() {
          {
            setCodings(Arrays.asList("English"));
            setText("English");
          }
        });
      }
    });
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

  private MedicationAdministration createMedicationAdministration(
      Patient patient, Encounter encounter) {

    MedicationAdministration administration = new MedicationAdministration();
    administration.setStatus(MedicationAdministrationStatus.IN_PROGRESS);
    administration.setEffectiveTimePeriod(new Interval<>(Instant.now(), Instant.now()));
    administration.setPatientId(patient.getId());
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
    Patient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter();
    encounterRepository.save(patient, encounter);

    MedicationAdministration administration = createMedicationAdministration(patient, encounter);
    medicationAdministrationRepository.save(patient, encounter, administration);

    List<MedicationAdministration> administrations =
        medicationAdministrationRepository.list(patient.getId(), encounter.getId());
    assertEquals(administrations.get(0), administration);
  }
}
