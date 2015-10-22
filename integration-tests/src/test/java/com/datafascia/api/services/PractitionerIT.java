// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import com.datafascia.domain.fhir.IdentifierSystems;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Integration tests for practitioner resources.
 */
@Slf4j
public class PractitionerIT extends ApiTestSupport {
  private static final String PRACTITIONER1 = "UCSPractitioner1-6789";
  private static final String PRACTITIONER2 = "UCSPractitioner2-6789";

  /**
   * Validates Practitioner retrieval.
   */
  @Test
  public void should_list_practitioners() {
    Practitioner practitioner1 = createPractitioner(PRACTITIONER1);
    MethodOutcome outcome = client.create().resource(practitioner1)
        .encodedJson().execute();
    practitioner1.setId(outcome.getId());

    Practitioner practitioner2 = createPractitioner(PRACTITIONER2);
    outcome = client.create().resource(practitioner2)
        .encodedJson().execute();
    practitioner2.setId(outcome.getId());

    Bundle results = client.search().forResource(Practitioner.class).execute();

    List<IResource> practitioners = ApiUtil.extractBundle(results, Practitioner.class);
    assertTrue(practitioners.size() >= 2, "No-argument search failed.");
    for (IResource resource : practitioners) {
      Practitioner result = (Practitioner) resource;
      switch (result.getIdentifierFirstRep().getValue()) {
        case PRACTITIONER1:
          assertEquals(result.getId().getIdPart(), practitioner1.getId().getIdPart());
          break;
        case PRACTITIONER2:
          assertEquals(result.getId().getIdPart(), practitioner2.getId().getIdPart());
          break;
      }
    }

    Practitioner patient = client.read()
        .resource(Practitioner.class)
        .withId(practitioner1.getId().getIdPart())
        .execute();
    assertEquals(patient.getId().getIdPart(), practitioner1.getId().getIdPart());
  }

  private Practitioner createPractitioner(String id) {
    Practitioner practitioner = new Practitioner();
    practitioner.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_PRACTITIONER).setValue(id);
    practitioner.setName(new HumanNameDt().addGiven("firstname").addGiven("middlename").addFamily(
        "lastname"));
    practitioner
        .setGender(AdministrativeGenderEnum.MALE)
        .setBirthDate(new DateDt(new Date()));
    return practitioner;
  }
}
