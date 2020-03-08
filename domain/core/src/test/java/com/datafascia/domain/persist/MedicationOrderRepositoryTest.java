// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
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
 * {@link MedicationOrderRepository} test
 */
public class MedicationOrderRepositoryTest extends RepositoryTestSupport {

  @Inject
  private PatientRepository patientRepository;

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private MedicationOrderRepository medicationOrderRepository;

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

  private MedicationOrder createMedicationOrder(
      UnitedStatesPatient patient, Encounter encounter) {

    MedicationOrder prescription = new MedicationOrder();
    prescription.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ORDER)
        .setValue("medicationPrescriptionId");
    prescription.setMedication(new ResourceReferenceDt("medicationID"));
    prescription.setPatient(new ResourceReferenceDt(patient));
    prescription.setEncounter(new ResourceReferenceDt(encounter));
    return prescription;
  }

  @Test
  public void should_list_and_read_medication_orders() {
    UnitedStatesPatient patient = createPatient();
    patientRepository.save(patient);

    Encounter encounter = createEncounter(patient);
    encounterRepository.save(encounter);

    MedicationOrder prescription = createMedicationOrder(patient, encounter);
    medicationOrderRepository.save(prescription);

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    List<MedicationOrder> medicationOrders = medicationOrderRepository.list(encounterId);
    assertEquals(medicationOrders.get(0).getId().getIdPart(), prescription.getId().getIdPart());

    Id<MedicationOrder> orderId = MedicationOrderRepository.generateId(prescription);
    Optional<MedicationOrder> resultPrescription = medicationOrderRepository.read(
        encounterId, orderId);
    assertTrue(resultPrescription.isPresent(), "Read operation failed.");
    assertEquals(resultPrescription.get().getId().getIdPart(),
        prescription.getId().getIdPart(), "Read operation failed.");
  }
}
