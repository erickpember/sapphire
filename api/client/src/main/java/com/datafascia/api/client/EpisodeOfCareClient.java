// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.EpisodeOfCare;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Client utilities for episodes of care.
 */
public class EpisodeOfCareClient  extends BaseClient<EpisodeOfCare> {
  /**
   * Builds a EpisodeOfCareClient
   *
   * @param client The FHIR client to use.
   */
  public EpisodeOfCareClient(IGenericClient client) {
    super(client);
  }

  /**
   * Returns a EpisodeOfCare for a given patient ID and episode ID.
   *
   * @param episodeId
   *    The episode ID.
   * @return
   *    A MedicationAdministrationClient
   */
  public EpisodeOfCare get(String episodeId) {
    return client.read()
        .resource(EpisodeOfCare.class)
        .withId(episodeId)
        .execute();
  }

  /**
   * Save a given EpisodeOfCare.
   *
   * @param episode
   *    The EpisodeOfCare to save.
   * @return
   *    The EpisodeOfCare with populated native ID.
   */
  public EpisodeOfCare save(EpisodeOfCare episode) {
    MethodOutcome outcome = client.create().resource(episode).execute();
    episode.setId(outcome.getId());
    return episode;
  }

  /**
   * Updates a EpisodeOfCare.
   *
   * @param episode
   *    The EpisodeOfCare to update.
   */
  public void update(EpisodeOfCare episode) {
    client.update().resource(episode).execute();
  }
}
