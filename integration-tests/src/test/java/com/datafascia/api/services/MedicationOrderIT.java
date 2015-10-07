// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Integration tests for MedicationOrder resources.
 */
@Slf4j
public class MedicationOrderIT extends ApiTestSupport {
  /**
   * Validates MedicationPrescription retrieval.
   *
   */
  @Test
  public void should_list_medication_orders() {
    UnitedStatesPatient patient = createPatient();
    MethodOutcome outcome = client.create().resource(patient)
        .encodedJson().execute();
    patient.setId(outcome.getId());

    Encounter encounter = createEncounter(patient);

    outcome = client.create().resource(encounter)
        .encodedJson().execute();
    encounter.setId(outcome.getId());

    MedicationOrder medicationOrder1
        = createMedicationOrder("1", patient, encounter);
    medicationOrder1.setEncounter(new ResourceReferenceDt(encounter));
    outcome = client.create().resource(medicationOrder1)
        .encodedJson().execute();
    medicationOrder1.setId(outcome.getId());

    MedicationOrder medicationOrder2
        = createMedicationOrder("2", patient, encounter);
    medicationOrder1.setEncounter(new ResourceReferenceDt(encounter));
    outcome = client.create().resource(medicationOrder2)
        .encodedJson().execute();
    medicationOrder2.setId(outcome.getId());

    Bundle results = client.search().forResource(MedicationOrder.class)
        .where(new StringClientParam("encounter")
            .matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    List<IResource> medicationOrders = ApiUtil.extractBundle(results, MedicationOrder.class);
    assertEquals(medicationOrders.size(), 2);
    for (IResource resource : medicationOrders) {
      MedicationOrder medicationOrder = (MedicationOrder) resource;
      switch (medicationOrder.getIdentifierFirstRep().getValue()) {
        case "1":
          assertEquals(medicationOrder.getId().getIdPart(), medicationOrder1.getId().getIdPart());
          break;
        case "2":
          assertEquals(medicationOrder.getId().getIdPart(), medicationOrder2.getId().getIdPart());
          break;
        default:
          fail("unexpected medicationPrescription identifier:" +
              medicationOrder.getIdentifierFirstRep().getValue());
      }
    }

    results = client.search().forResource(MedicationOrder.class)
        .where(new StringClientParam(MedicationOrder.SP_ENCOUNTER)
            .matches()
            .value(encounter.getId().getIdPart()))
        .where(new StringClientParam(MedicationOrder.SP_RES_ID)
            .matches()
            .value(medicationOrder1.getId().getIdPart()))
        .execute();
    medicationOrders = ApiUtil.extractBundle(results, MedicationOrder.class);
    assertEquals(medicationOrders.size(), 1, "Two argument read/search failed.");
    assertEquals(medicationOrders.get(0).getId().getIdPart(),
        medicationOrder1.getId().getIdPart(), "Two argument read/search failed.");

    results = client.search().forResource(MedicationOrder.class)
        .execute();
    medicationOrders = ApiUtil.extractBundle(results, MedicationOrder.class);
    assertTrue(medicationOrders.size() > 0, "No-argument search failed.");

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

  private MedicationOrder createMedicationOrder(
      String identifier, UnitedStatesPatient patient, Encounter encounter) {

    MedicationOrder prescription = new MedicationOrder();
    prescription.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_MEDICATION_ORDER)
        .setValue(identifier);
    prescription.setMedication(new ResourceReferenceDt("medicationID"));
    prescription.setPatient(new ResourceReferenceDt(patient));
    prescription.setEncounter(new ResourceReferenceDt(encounter));
    return prescription;
  }
}
