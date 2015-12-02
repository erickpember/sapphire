// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Integration tests for encounter resources.
 */
@Slf4j
public class EncounterIT extends ApiTestSupport {

  @Test
  public void should_search_encounters_by_status() throws Exception {
    Bundle results = client.search()
        .forResource(Encounter.class)
        .where(Encounter.STATUS.exactly().code(EncounterStateEnum.IN_PROGRESS.name()))
        .execute();

    List<IResource> encounters = ApiUtil.extractBundle(results, Encounter.class);

    assertTrue(encounters.size() > 1);

    results = client.search()
        .forResource(Encounter.class)
        .execute();
    encounters = ApiUtil.extractBundle(results, Encounter.class);
    assertTrue(encounters.size() > 0, "No-argument search failed.");
  }

  @Test(expectedExceptions = ResourceNotFoundException.class)
  public void should_not_find_encounter() {
    client.read()
        .resource(Encounter.class)
        .withId("sasquatch")
        .execute();
  }
}
