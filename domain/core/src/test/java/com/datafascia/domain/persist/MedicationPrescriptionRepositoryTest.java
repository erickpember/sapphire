// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
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
 * {@link MedicationPrescriptionRepository} test
 */
public class MedicationPrescriptionRepositoryTest extends RepositoryTestSupport {

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private MedicationPrescriptionRepository medicationPrescriptionRepository;

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue("UCSF-12345");
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("12345");
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
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("12345");
    encounter
        .setPeriod(period)
        .setPatient(new ResourceReferenceDt(patient.getId()));
    return encounter;
  }

  private MedicationPrescription createMedicationPrescription(
      UnitedStatesPatient patient, Encounter encounter) {

    MedicationPrescription prescription = new MedicationPrescription();
    prescription.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_PRESCRIPTION)
        .setValue("medicationPrescriptionId");
    prescription.setMedication(new ResourceReferenceDt("medicationID"));
    prescription.setPatient(new ResourceReferenceDt(patient));
    prescription.setEncounter(new ResourceReferenceDt(encounter));
    return prescription;
  }

  @Test
  public void should_list_and_read_medication_prescriptions() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter(patient);
    encounterRepository.save(encounter);

    MedicationPrescription prescription = createMedicationPrescription(patient, encounter);
    medicationPrescriptionRepository.save(prescription);

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    List<MedicationPrescription> prescriptions = medicationPrescriptionRepository.list(
        encounterId);
    assertEquals(prescriptions.get(0).getId().getIdPart(), prescription.getId().getIdPart());

    Id<MedicationPrescription> adminId = MedicationPrescriptionRepository.generateId(
        prescription);
    Optional<MedicationPrescription> resultPrescription = medicationPrescriptionRepository.
        read(encounterId, adminId);
    assertTrue(resultPrescription.isPresent(), "Read operation failed.");
    assertEquals(resultPrescription.get().getId().getIdPart(),
        prescription.getId().getIdPart(), "Read operation failed.");
  }
}
