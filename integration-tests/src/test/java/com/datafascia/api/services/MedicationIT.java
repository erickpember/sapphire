// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for medication resources.
 */
@Slf4j
public class MedicationIT extends ApiTestSupport {

  @Test
  public void should_read_medication() throws Exception {
    String id = "code";
    Medication medication = client.read()
        .resource(Medication.class)
        .withId(id)
        .execute();

    assertEquals(medication.getId().getIdPart(), id);

    Bundle results = client.search().forResource(Medication.class).execute();
    List<IResource> medications = ApiUtil.extractBundle(results, Medication.class);
    assertTrue(medications.size() > 0, "No-argument search failed.");
  }

  @Test(expectedExceptions = ResourceNotFoundException.class)
  public void should_not_find_medication() {
    client.read()
        .resource(Medication.class)
        .withId("researchSupportingHomeopathy")
        .execute();
  }
}
