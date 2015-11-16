// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.Substance;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Client utilities for substances.
 */
public class SubstanceClient extends BaseClient<Substance> {
  /**
   * Builds a SubstanceClient
   *
   * @param client
   *    The FHIR client to use.
   */
  public SubstanceClient(IGenericClient client) {
    super(client);
  }

  /**
   * Save a given substance.
   *
   * @param substance
   *  The substance to save.
   * @return The substance with populated native ID.
   */
  public Substance saveSubstance(Substance substance) {
    MethodOutcome outcome = client.create().resource(substance).execute();
    substance.setId(outcome.getId());
    return substance;
  }

  /**
   * Returns a substance for a given id.
   *
   * @param id
   *    The substance resource ID
   * @return
   *    A substance instance.
   */
  public Substance getSubstance(String id) {
    return client.read()
        .resource(Substance.class)
        .withId(id)
        .execute();
  }
}
