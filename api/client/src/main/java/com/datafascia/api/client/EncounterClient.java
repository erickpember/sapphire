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

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.rest.client.IGenericClient;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Client utilities for encounters.
 */
public class EncounterClient extends BaseClient<Encounter> {

  private final LoadingCache<String, Encounter> idToEncounterMap =
      CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
          new CacheLoader<String, Encounter>() {
            @Override
            public Encounter load(String encounterId) {
              return read(encounterId);
            }
          });

  /**
   * Builds a EncounterClient
   *
   * @param client The FHIR client to use.
   */
  public EncounterClient(IGenericClient client) {
    super(client);
  }

  private Encounter read(String encounterId) {
    return client.read()
        .resource(Encounter.class)
        .withId(encounterId)
        .execute();
  }

  /**
   * Returns an encounter for a given Contact Serial Number (AKA Encounter Id).
   *
   * @param csn The Contact Serial Number.
   * @return An encounter.
   */
  public Encounter getEncounter(String csn) {
    try {
      return idToEncounterMap.getUnchecked(csn);
    } catch (UncheckedExecutionException e) {
      Throwables.propagateIfPossible(e.getCause());
      throw new IllegalStateException("Cannot read encounter", e.getCause());
    }
  }

  /**
   * Invalidates cache entry for a encounter.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidate(String encounterId) {
    idToEncounterMap.invalidate(encounterId);
  }
}
