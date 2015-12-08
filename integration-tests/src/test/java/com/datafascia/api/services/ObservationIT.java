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
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
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
 * Integration tests for observation resources.
 */
@Slf4j
public class ObservationIT extends ApiTestSupport {
  private static final String NUMERICAL_PAIN_LEVEL_LOW = "numericalPainLevelLow";
  private static final String NUMERICAL_PAIN_LEVEL_HIGH = "numericalPainLevelHigh";

  /**
   * Validates Observation retrieval.
   *
   */
  @Test
  public void should_list_observations() {
    UnitedStatesPatient patient = createPatient();
    MethodOutcome outcome = client.create().resource(patient)
        .encodedJson().execute();
    patient.setId(outcome.getId());

    Encounter encounter = createEncounter(patient);

    outcome = client.create().resource(encounter)
        .encodedJson().execute();
    encounter.setId(outcome.getId());

    Observation observation1 = createObservation(NUMERICAL_PAIN_LEVEL_LOW, "1");
    observation1.setEncounter(new ResourceReferenceDt(encounter));
    client.update().resource(observation1).encodedJson().execute();

    Observation observation2 = createObservation(NUMERICAL_PAIN_LEVEL_HIGH, "2");
    observation2.setEncounter(new ResourceReferenceDt(encounter));
    client.update().resource(observation2).encodedJson().execute();

    Bundle results = client.search().forResource(Observation.class)
        .where(new StringClientParam("encounter")
            .matches()
            .value(encounter.getId().getIdPart()))
        .execute();

    List<IResource> observations = ApiUtil.extractBundle(results, Observation.class);
    assertEquals(observations.size(), 2);
    for (IResource resource : observations) {
      Observation observation = (Observation) resource;
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case NUMERICAL_PAIN_LEVEL_LOW:
          assertEquals(observation.getId().getIdPart(), observation1.getId().getIdPart());
          break;
        case NUMERICAL_PAIN_LEVEL_HIGH:
          assertEquals(observation.getId().getIdPart(), observation2.getId().getIdPart(),
              "Single argument save failed.");
          break;
        default:
          fail("unexpected observation code:" + observation.getCode().getCodingFirstRep()
              .getCode());
      }
    }

    results = client.search().forResource(Observation.class).execute();
    observations = ApiUtil.extractBundle(results, Observation.class);
    assertTrue(observations.size() > 0, "No-argument search failed.");

    // Get rid of this particular encounter and patient so it doesn't mess up other tests.
    client.delete().resourceById(encounter.getId()).execute();
    client.delete().resourceById(patient.getId()).execute();
  }

  private UnitedStatesPatient createPatient() {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue("UCSF-6789");
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("6789");
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
        .setSystem(IdentifierSystems.INSTITUTION_ENCOUNTER).setValue("encounterForObservation");
    encounter
        .setPeriod(period)
        .setStatus(EncounterStateEnum.ARRIVED)
        .setPatient(new ResourceReferenceDt(patient.getId()));
    return encounter;
  }

  private Observation createObservation(String code, String value) {
    Observation observation = new Observation()
        .setCode(new CodeableConceptDt("system", code))
        .setValue(new StringDt(value))
        .setIssued(new Date(), TemporalPrecisionEnum.SECOND);
    observation.setId(new IdDt(Observation.class.getSimpleName(), code));
    return observation;
  }
}
