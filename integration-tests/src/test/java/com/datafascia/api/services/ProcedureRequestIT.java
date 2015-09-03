// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
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
 * Integration tests for ProcedureRequest resources.
 */
@Slf4j
public class ProcedureRequestIT extends ApiIT {
  /**
   * Validates ProcedureRequest retrieval.
   *
   */
  @Test
  public void should_list_procedureRequests() {
    UnitedStatesPatient patient = createPatient();
    MethodOutcome outcome = client.create().resource(patient).encodedJson().execute();
    patient.setId(outcome.getId());

    Encounter encounter = createEncounter(patient);

    outcome = client.create().resource(encounter).encodedJson().execute();
    encounter.setId(outcome.getId());

    ProcedureRequest procedureRequest1
        = createProcedureRequest("1", patient, encounter);
    procedureRequest1.setEncounter(new ResourceReferenceDt(encounter));
    outcome = client.create().resource(procedureRequest1)
        .encodedJson().execute();
    procedureRequest1.setId(outcome.getId());

    ProcedureRequest procedureRequest2
        = createProcedureRequest("2", patient, encounter);
    procedureRequest1.setEncounter(new ResourceReferenceDt(encounter));
    outcome = client.create().resource(procedureRequest2)
        .encodedJson().execute();
    procedureRequest2.setId(outcome.getId());

    Bundle results = client.search().forResource(ProcedureRequest.class)
        .where(new StringClientParam("encounter")
            .matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    List<IResource> procedureRequests = ApiUtil.extractBundle(results,
        ProcedureRequest.class);
    assertEquals(procedureRequests.size(), 2);
    for (IResource resource : procedureRequests) {
      ProcedureRequest procedureRequest = (ProcedureRequest) resource;
      switch (procedureRequest.getIdentifierFirstRep().getValue()) {
        case "1":
          assertEquals(procedureRequest.getId().getIdPart(), procedureRequest1.
              getId().getIdPart());
          break;
        case "2":
          assertEquals(procedureRequest.getId().getIdPart(), procedureRequest2.
              getId().getIdPart());
          break;
        default:
          fail("unexpected procedureRequest identifier:" + procedureRequest.
              getIdentifierFirstRep().getValue());
      }
    }

    results = client.search().forResource(ProcedureRequest.class)
        .where(new StringClientParam(ProcedureRequest.SP_ENCOUNTER)
            .matches()
            .value(encounter.getId().getIdPart()))
        .where(new StringClientParam(ProcedureRequest.SP_RES_ID)
            .matches()
            .value(procedureRequest1.getId().getIdPart()))
        .execute();
    procedureRequests = ApiUtil.extractBundle(results,
        ProcedureRequest.class);
    assertEquals(procedureRequests.size(), 1, "Two argument read/search failed.");
    assertEquals(procedureRequests.get(0).getId().getIdPart(),
        procedureRequest1.getId().getIdPart(), "Two argument read/search failed.");

    results = client.search().forResource(ProcedureRequest.class)
        .execute();
    procedureRequests = ApiUtil.extractBundle(results,
        ProcedureRequest.class);
    assertTrue(procedureRequests.size() > 0, "No-argument search failed.");

    // Get rid of this particular encounter and patient so it doesn't mess up other tests.
    client.delete().resourceById(encounter.getId()).execute();
    client.delete().resourceById(patient.getId()).execute();
  }

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue("UCSF-PR-6789");
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("PR6789");
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
            "encounterForProcedureRequest");
    encounter
        .setPeriod(period)
        .setStatus(EncounterStateEnum.ARRIVED)
        .setPatient(new ResourceReferenceDt(patient.getId()));
    return encounter;
  }

  private ProcedureRequest createProcedureRequest(String identifier,
      UnitedStatesPatient patient, Encounter encounter) {

    ProcedureRequest request = new ProcedureRequest();
    request.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PROCEDURE_REQUEST)
        .setValue(identifier);
    request.setSubject(new ResourceReferenceDt(patient));
    request.setEncounter(new ResourceReferenceDt(encounter));
    return request;
  }
}
