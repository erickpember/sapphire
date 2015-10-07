// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.FlagStatusEnum;
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
import static org.testng.Assert.fail;

/**
 * Integration tests for flag resources.
 */
@Slf4j
public class FlagIT extends ApiTestSupport {
  private static final String ADVANCE_DIRECTIVE = "230218701ADPOA";
  private static final String PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT = "230218701POLST";

  /**
   * Validates Flag retrieval.
   *
   */
  @Test
  public void should_list_flags() {
    UnitedStatesPatient patient = createPatient("UCSFlag1-6789");
    MethodOutcome outcome = client.create().resource(patient)
        .encodedJson().execute();
    patient.setId(outcome.getId());

    UnitedStatesPatient patient2 = createPatient("UCSFlag2-6789");
    outcome = client.create().resource(patient2)
        .encodedJson().execute();
    patient2.setId(outcome.getId());

    Flag flag1 = createFlag(ADVANCE_DIRECTIVE);
    flag1.setSubject(new ResourceReferenceDt(patient));
    outcome = client.create().resource(flag1)
        .encodedJson().execute();
    flag1.setId(outcome.getId());

    Flag flag2 = createFlag(PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT);
    flag2.setSubject(new ResourceReferenceDt(patient2));
    outcome = client.create().resource(flag2)
        .encodedJson().execute();
    flag2.setId(outcome.getId());

    Bundle  results = client.search().forResource(Flag.class).execute();

    List<IResource> flags = ApiUtil.extractBundle(results, Flag.class);
    assertEquals(flags.size(), 2, "No-argument search failed.");
    for (IResource resource : flags) {
      Flag flag = (Flag) resource;
      switch (flag.getCode().getCodingFirstRep().getCode()) {
        case ADVANCE_DIRECTIVE:
          assertEquals(flag.getId().getIdPart(), flag1.getId().getIdPart());
          break;
        case PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT:
          assertEquals(flag.getId().getIdPart(), flag2.getId().getIdPart());
          break;
        default:
          fail("unexpected flag code:" + flag.getCode().getCodingFirstRep()
              .getCode());
      }
    }

    results = client.search().forResource(Flag.class)
        .where(new StringClientParam("patient")
            .matches()
            .value(patient.getId().getIdPart()))
        .execute();

    flags = ApiUtil.extractBundle(results, Flag.class);
    assertEquals(flags.size(), 1);

    // Get rid of this particular patient so it doesn't mess up other tests.
    client.delete().resourceById(patient.getId()).execute();
    client.delete().resourceById(patient2.getId()).execute();
  }

  private UnitedStatesPatient createPatient(String id) {
    UnitedStatesPatient patient = new UnitedStatesPatient();
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PATIENT).setValue(id);
    patient.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_BILLING_ACCOUNT).setValue("Flag-6789");
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

  private Flag createFlag(String code) {
    Flag flag = new Flag();
    flag.setCode(new CodeableConceptDt("system", code))
        .setStatus(FlagStatusEnum.ENTERED_IN_ERROR);
    return flag;
  }
}
