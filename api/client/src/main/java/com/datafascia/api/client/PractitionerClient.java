// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Client utilities for practitioners.
 */
public class PractitionerClient extends BaseClient<Practitioner> {
  /**
   * Builds a PractitionerClient
   *
   * @param client
   *    The FHIR client to use.
   */
  public PractitionerClient(IGenericClient client) {
    super(client);
  }

  /**
   * Returns a practitioner for a given id.
   *
   * @param id
   *    The practitioner resource ID
   * @return
   *    A practitioner instance.
   */
  public Practitioner getPractitioner(String id) {
    return client.read()
        .resource(Practitioner.class)
        .withId(id)
        .execute();
  }
}
