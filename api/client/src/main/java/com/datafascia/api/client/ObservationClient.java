// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Client utilities for Observation resources.
 */
public class ObservationClient extends BaseClient<Observation> {

  private LoadingCache<String, List<Observation>> encounterIdToObservationsMap =
      CacheBuilder.newBuilder()
      .expireAfterWrite(5, TimeUnit.SECONDS)
      .build(
          new CacheLoader<String, List<Observation>>() {
            public List<Observation> load(String encounterId) {
              return list(encounterId);
            }
          });

  /**
   * Builds a ObservationClient
   *
   * @param client The FHIR client to use.
   */
  public ObservationClient(IGenericClient client) {
    super(client);
  }

  private List<Observation> list(String encounterId) {
    Bundle results = client.search()
        .forResource(Observation.class)
        .where(new StringClientParam(Observation.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();

    return extractBundle(results, Observation.class);
  }

  /**
   * Searches observations
   *
   * @param encounterId
   *     encounter to which the observations belong
   * @param code
   *     code of observation, optional
   * @param status
   *     status of observation, optional
   * @return observations
   */
  public List<Observation> searchObservation(String encounterId, String code, String status) {
    List<Observation> observations = encounterIdToObservationsMap.getUnchecked(encounterId);
    if (Strings.isNullOrEmpty(code) && Strings.isNullOrEmpty(status)) {
      return observations;
    }

    return observations.stream()
        .filter(observation ->
            Strings.isNullOrEmpty(code) ||
            code.equals(observation.getCode().getCodingFirstRep().getCode()))
        .filter(observation ->
            Strings.isNullOrEmpty(status) ||
            status.equals(observation.getStatus()))
        .collect(Collectors.toList());
  }

  /**
   * Invalidates cache entry for encounter.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidate(String encounterId) {
    encounterIdToObservationsMap.invalidate(encounterId);
  }
}
