// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

/**
 * Client utilities for Observation resources.
 */
public class ObservationClient extends BaseClient<Observation> {
  /**
   * Builds a ObservationClient
   *
   * @param client The FHIR client to use.
   */
  public ObservationClient(IGenericClient client) {
    super(client);
  }

  /**
   * Save a given Observation.
   *
   * @param observation
   *  The Observation to save.
   * @return The Observation with populated native ID.
   */
  public Observation saveObservation(Observation observation) {
    MethodOutcome outcome = client.create().resource(observation).execute();
    observation.setId(outcome.getId());
    return observation;
  }

  /**
   * Updates a Observation.
   *
   * @param observation The Observation to update.
   */
  public void updateObservation(Observation observation) {
    client.update().resource(observation).execute();
  }

  /**
   * Searches Observations
   *
   * @param encounterId
   *  The ID of the encounter to which the observations belong.
   * @param code
   *  Code of observation, optional.
   * @param status
   *  Status of observation, optional.
   * @return A list Observations.
   */
  public List<Observation> searchObservation(String encounterId, String code,
      String status) {
    Bundle results = client.search().forResource(Observation.class)
        .where(new StringClientParam(Observation.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();

    List<Observation> observations = extractBundle(results, Observation.class);

    if (!Strings.isNullOrEmpty(code)) {
      List<Observation> filteredResults = new ArrayList<>();
      for (Observation observation : observations) {
        if (observation.getCode().getCodingFirstRep().getCode().equalsIgnoreCase(code)) {
          filteredResults.add(observation);
        }
      }
      observations = filteredResults;
    }

    if (!Strings.isNullOrEmpty(status)) {
      List<Observation> filteredResults = new ArrayList<>();
      for (Observation observation : observations) {
        if (observation.getStatus().equalsIgnoreCase(status)) {
          filteredResults.add(observation);
        }
      }
      observations = filteredResults;
    }

    return observations;
  }
}
