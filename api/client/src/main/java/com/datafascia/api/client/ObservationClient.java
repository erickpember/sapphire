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

import ca.uhn.fhir.model.dstu2.resource.Bundle;
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

  private LoadingCache<String, List<Observation>> encounterIdToObservationListMap =
      CacheBuilder.newBuilder()
      .expireAfterWrite(30, TimeUnit.SECONDS)
      .build(
          new CacheLoader<String, List<Observation>>() {
            @Override
            public List<Observation> load(String encounterId) {
              return search(encounterId);
            }
          });

  private final LoadingCache<String, Observations> encounterIdToObservationsMap =
      CacheBuilder.newBuilder()
      .expireAfterWrite(30, TimeUnit.SECONDS)
      .build(
          new CacheLoader<String, Observations>() {
            @Override
            public Observations load(String encounterId) {
              return new Observations(encounterIdToObservationListMap.getUnchecked(encounterId));
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

  private List<Observation> search(String encounterId) {
    Bundle results = client.search()
        .forResource(Observation.class)
        .where(new StringClientParam(Observation.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .returnBundle(Bundle.class)
        .execute();

    return extractBundle(results, Observation.class);
  }

  /**
   * Lists observations for an encounter.
   *
   * @param encounterId
   *     encounter to which the observations belong
   * @return observations
   */
  public Observations list(String encounterId) {
    return encounterIdToObservationsMap.getUnchecked(encounterId);
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
    List<Observation> observations = encounterIdToObservationListMap.getUnchecked(encounterId);
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
    encounterIdToObservationListMap.invalidate(encounterId);
    encounterIdToObservationsMap.invalidate(encounterId);
  }
}
