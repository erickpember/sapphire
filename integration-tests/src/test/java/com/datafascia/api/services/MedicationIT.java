// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.model.dstu2.resource.Medication;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration tests for medication resources.
 */
@Slf4j
public class MedicationIT extends ApiIT {
  /**
   * Validates Medication retrieval.
   *
   * @throws Exception
   */
  @Test
  public void testMedication() throws Exception {
    String id = "code";
    Medication medication = client.read()
        .resource(Medication.class)
        .withId(id)
        .execute();

    assertEquals(medication.getId().getIdPart(), id);
  }
}
