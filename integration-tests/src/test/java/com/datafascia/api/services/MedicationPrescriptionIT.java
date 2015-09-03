// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.Languages;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.neovisionaries.i18n.LanguageCode;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static com.datafascia.api.services.ApiIT.client;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Integration tests for MedicationPrescription resources.
 */
@Slf4j
public class MedicationPrescriptionIT extends ApiIT {
  /**
   * Validates MedicationPrescription retrieval.
   *
   */
  @Test
  public void should_list_medicationPrescriptions() {
    UnitedStatesPatient patient = createPatient();
    MethodOutcome outcome = client.create().resource(patient)
        .encodedJson().execute();
    patient.setId(outcome.getId());

    Encounter encounter = createEncounter(patient);

    outcome = client.create().resource(encounter)
        .encodedJson().execute();
    encounter.setId(outcome.getId());

    MedicationPrescription medicationPrescription1
        = createMedicationPrescription("1", patient, encounter);
    medicationPrescription1.setEncounter(new ResourceReferenceDt(encounter));
    outcome = client.create().resource(medicationPrescription1)
        .encodedJson().execute();
    medicationPrescription1.setId(outcome.getId());

    MedicationPrescription medicationPrescription2
        = createMedicationPrescription("2", patient, encounter);
    medicationPrescription1.setEncounter(new ResourceReferenceDt(encounter));
    outcome = client.create().resource(medicationPrescription2)
        .encodedJson().execute();
    medicationPrescription2.setId(outcome.getId());

    Bundle results = client.search().forResource(MedicationPrescription.class)
        .where(new StringClientParam("encounter")
            .matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    List<IResource> medicationPrescriptions = ApiUtil.extractBundle(results,
        MedicationPrescription.class);
    assertEquals(medicationPrescriptions.size(), 2);
    for (IResource resource : medicationPrescriptions) {
      MedicationPrescription medicationPrescription = (MedicationPrescription) resource;
      switch (medicationPrescription.getIdentifierFirstRep().getValue()) {
        case "1":
          assertEquals(medicationPrescription.getId().getIdPart(), medicationPrescription1.
              getId().getIdPart());
          break;
        case "2":
          assertEquals(medicationPrescription.getId().getIdPart(), medicationPrescription2.
              getId().getIdPart());
          break;
        default:
          fail("unexpected medicationPrescription identifier:" + medicationPrescription.
              getIdentifierFirstRep().getValue());
      }
    }

    results = client.search().forResource(MedicationPrescription.class)
        .where(new StringClientParam(MedicationPrescription.SP_ENCOUNTER)
            .matches()
            .value(encounter.getId().getIdPart()))
        .where(new StringClientParam(MedicationPrescription.SP_RES_ID)
            .matches()
            .value(medicationPrescription1.getId().getIdPart()))
        .execute();
    medicationPrescriptions = ApiUtil.extractBundle(results,
        MedicationPrescription.class);
    assertEquals(medicationPrescriptions.size(), 1, "Two argument read/search failed.");
    assertEquals(medicationPrescriptions.get(0).getId().getIdPart(),
        medicationPrescription1.getId().getIdPart(), "Two argument read/search failed.");

    results = client.search().forResource(MedicationPrescription.class)
        .execute();
    medicationPrescriptions = ApiUtil.extractBundle(results,
        MedicationPrescription.class);
    assertTrue(medicationPrescriptions.size() > 0, "No-argument search failed.");

    // Get rid of this particular encounter and patient so it doesn't mess up other tests.
    client.delete().resourceById(encounter.getId()).execute();
    client.delete().resourceById(patient.getId()).execute();
  }

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue("UCSF-RX-6789");
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("RX6789");
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
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue(
            "encounterForMedicationPrescription");
    encounter
        .setPeriod(period)
        .setStatus(EncounterStateEnum.ARRIVED)
        .setPatient(new ResourceReferenceDt(patient.getId()));
    return encounter;
  }

  private MedicationPrescription createMedicationPrescription(String identifier,
      UnitedStatesPatient patient, Encounter encounter) {

    MedicationPrescription prescription = new MedicationPrescription();
    prescription.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_PRESCRIPTION)
        .setValue(identifier);
    prescription.setMedication(new ResourceReferenceDt("medicationID"));
    prescription.setPatient(new ResourceReferenceDt(patient));
    prescription.setEncounter(new ResourceReferenceDt(encounter));
    return prescription;
  }
}
