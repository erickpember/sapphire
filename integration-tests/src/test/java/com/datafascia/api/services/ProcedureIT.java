// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.datafascia.domain.fhir.CodingSystems;
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
 * Integration tests for procedure resources.
 */
@Slf4j
public class ProcedureIT extends ApiTestSupport {
  private static final String TYPE_CODE1 = "typeCode1";
  private static final String TYPE_CODE2 = "typeCode2";

  /**
   * Validates Procedure retrieval.
   */
  @Test
  public void should_list_procedures() {
    UnitedStatesPatient patient = createPatient();
    MethodOutcome outcome = client.create().resource(patient)
        .encodedJson().execute();
    patient.setId(outcome.getId());

    Encounter encounter = createEncounter(patient);

    outcome = client.create().resource(encounter).encodedJson().execute();
    encounter.setId(outcome.getId());

    Procedure procedure1 = createProcedure(TYPE_CODE1, "1", encounter);
    Procedure procedure2 = createProcedure(TYPE_CODE2, "2", encounter);

    outcome = client.create().resource(procedure1).encodedJson().execute();
    procedure1.setId(outcome.getId());

    outcome = client.create().resource(procedure2).encodedJson().execute();
    procedure2.setId(outcome.getId());

    Bundle results = client.search().forResource(Procedure.class)
        .where(new StringClientParam("encounter")
            .matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    List<IResource> procedures = ApiUtil.extractBundle(results, Procedure.class);
    assertEquals(procedures.size(), 2);
    for (IResource resource : procedures) {
      Procedure procedure = (Procedure) resource;
      switch (procedure.getCode().getCodingFirstRep().getCode()) {
        case TYPE_CODE1:
          assertEquals(procedure.getId().getIdPart(), procedure1.getId().getIdPart());
          break;
        case TYPE_CODE2:
          assertEquals(procedure.getId().getIdPart(), procedure2.getId().getIdPart());
          break;
        default:
          fail("unexpected procedure type:" + procedure.getCode().getCodingFirstRep()
              .getCode());
      }
    }

    results = client.search().forResource(Procedure.class).execute();
    procedures = ApiUtil.extractBundle(results, Procedure.class);
    assertTrue(procedures.size() > 0, "No-argument search failed.");

    // Get rid of this particular encounter and patient so it doesn't mess up other tests.
    client.delete().resourceById(encounter.getId()).execute();
    client.delete().resourceById(patient.getId()).execute();
  }

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue("UCSF-6789-Procedure");
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("6789-Procedure");
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
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("encounterForProcedure");
    encounter
        .setPeriod(period)
        .setStatus(EncounterStateEnum.ARRIVED)
        .setPatient(new ResourceReferenceDt(patient.getId()));
    return encounter;
  }

  private Procedure createProcedure(String typeCode, String bodySiteCode, Encounter encounter) {
    Procedure procedure = new Procedure()
        .setCode(new CodeableConceptDt(CodingSystems.PROCEDURE, typeCode))
        .setPerformed(new DateTimeDt(new Date(), TemporalPrecisionEnum.SECOND))
        .setEncounter(new ResourceReferenceDt(encounter.getId()));
    procedure.addBodySite(
        new CodeableConceptDt(CodingSystems.BODY_SITE, bodySiteCode));
    return procedure;
  }
}
