// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Integration tests for medicationAdministration resources.
 */
@Slf4j
public class MedicationAdministrationIT extends ApiIT {
  /**
   * Validates MedicationAdministration retrieval.
   *
   */
  @Test
  public void should_list_medicationAdministrations() {
    UnitedStatesPatient patient = createPatient();
    MethodOutcome outcome = client.create().resource(patient)
        .encodedJson().execute();
    patient.setId(outcome.getId());

    Encounter encounter = createEncounter(patient);

    outcome = client.create().resource(encounter)
        .encodedJson().execute();
    encounter.setId(outcome.getId());

    MedicationAdministration medicationAdministration1
        = createMedicationAdministration("1", patient, encounter);
    medicationAdministration1.setEncounter(new ResourceReferenceDt(encounter));
    outcome = client.create().resource(medicationAdministration1)
        .encodedJson().execute();
    medicationAdministration1.setId(outcome.getId());

    MedicationAdministration medicationAdministration2
        = createMedicationAdministration("2", patient, encounter);
    medicationAdministration1.setEncounter(new ResourceReferenceDt(encounter));
    outcome = client.create().resource(medicationAdministration2)
        .encodedJson().execute();
    medicationAdministration2.setId(outcome.getId());

    Bundle results = client.search().forResource(MedicationAdministration.class)
        .where(new StringClientParam("encounter")
            .matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    List<IResource> medicationAdministrations = ApiUtil.extractBundle(results,
        MedicationAdministration.class);
    assertEquals(medicationAdministrations.size(), 2);
    for (IResource resource : medicationAdministrations) {
      MedicationAdministration medicationAdministration = (MedicationAdministration) resource;
      switch (medicationAdministration.getIdentifierFirstRep().getValue()) {
        case "1":
          assertEquals(medicationAdministration.getId().getIdPart(), medicationAdministration1.
              getId().getIdPart());
          break;
        case "2":
          assertEquals(medicationAdministration.getId().getIdPart(), medicationAdministration2.
              getId().getIdPart());
          break;
        default:
          fail("unexpected medicationAdministration identifier:" + medicationAdministration.
              getIdentifierFirstRep().getValue());
      }
    }

    results = client.search().forResource(MedicationAdministration.class)
        .where(new StringClientParam(MedicationAdministration.SP_ENCOUNTER)
            .matches()
            .value(encounter.getId().getIdPart()))
        .where(new StringClientParam(MedicationAdministration.SP_RES_ID)
            .matches()
            .value(medicationAdministration1.getId().getIdPart()))
        .execute();
    medicationAdministrations = ApiUtil.extractBundle(results,
        MedicationAdministration.class);
    assertEquals(medicationAdministrations.size(), 1, "Two argument read/search failed.");
    assertEquals(medicationAdministrations.get(0).getId().getIdPart(),
        medicationAdministration1.getId().getIdPart(), "Two argument read/search failed.");

    results = client.search().forResource(MedicationAdministration.class)
        .execute();
    medicationAdministrations = ApiUtil.extractBundle(results,
        MedicationAdministration.class);
    assertTrue(medicationAdministrations.size() > 1, "No-argument search failed.");

    // Get rid of this particular encounter and patient so it doesn't mess up other tests.
    client.delete().resourceById(encounter.getId()).execute();
    client.delete().resourceById(patient.getId()).execute();
  }

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue("UCSF-MA-6789");
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("MA6789");
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
            "encounterForMedicationAdministration");
    encounter
        .setPeriod(period)
        .setStatus(EncounterStateEnum.ARRIVED)
        .setPatient(new ResourceReferenceDt(patient.getId()));
    return encounter;
  }

  private MedicationAdministration createMedicationAdministration(String identifier,
      UnitedStatesPatient patient, Encounter encounter) {

    MedicationAdministration administration = new MedicationAdministration();
    administration.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ADMINISTRATION)
        .setValue(identifier);
    administration.setStatus(MedicationAdministrationStatusEnum.IN_PROGRESS);
    administration.setMedication(new ResourceReferenceDt("medicationID"));
    MedicationAdministration.Dosage dosage = new MedicationAdministration.Dosage();
    dosage.setSite(new CodeableConceptDt("site", "site"));
    dosage.setRoute(new CodeableConceptDt("route", "route"));
    dosage.setMethod(new CodeableConceptDt("method", "method"));

    dosage.setQuantity(new SimpleQuantityDt(9000));
    dosage.setRate(new RatioDt());
    administration.setDosage(dosage);
    administration.setPatient(new ResourceReferenceDt(patient));
    administration.setEncounter(new ResourceReferenceDt(encounter));
    return administration;
  }
}
