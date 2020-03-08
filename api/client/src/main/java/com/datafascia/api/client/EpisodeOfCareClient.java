// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
