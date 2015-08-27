// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Client utilities for encounters.
 */
public class EncounterClient {
  private final IGenericClient client;

  /**
   * Builds a EncounterClient
   *
   * @param client The FHIR client to use.
   */
  public EncounterClient(IGenericClient client) {
    this.client = client;
  }

  /**
   * Returns an encounter for a given Contact Serial Number.
   *
   * @param csn The Contact Serial Number.
   * @return An encounter.
   */
  public Encounter getEncounter(String csn) {
    return client.read()
        .resource(Encounter.class)
        .withId(csn)
        .execute();
  }
}
