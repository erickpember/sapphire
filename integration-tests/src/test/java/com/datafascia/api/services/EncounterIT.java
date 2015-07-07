// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration tests for encounter resources.
 */
@Slf4j
public class EncounterIT extends ApiIT {

  /**
   * Validates Encounter searching.
   *
   * @throws Exception
   */
  @Test
  public void testEncounter() throws Exception {
    // Test filtering by an encounter state code.
    Bundle results = client.search()
            .forResource(Encounter.class)
            .where(Encounter.STATUS.exactly().code(EncounterStateEnum.IN_PROGRESS.name()))
            .execute();

    List<IResource> encounters = ApiUtil.extractBundle(results, Encounter.class);

    assertEquals(encounters.size(), 2);

    results = client.search()
            .forResource(Encounter.class)
            .execute();
    encounters = ApiUtil.extractBundle(results, Encounter.class);
    assertEquals(encounters.size(), 3);
  }
}
