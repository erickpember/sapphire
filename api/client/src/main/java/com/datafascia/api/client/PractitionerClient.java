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

import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.rest.client.IGenericClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

/**
 * Client utilities for practitioners.
 */
public class PractitionerClient extends BaseClient<Practitioner> {
  private final LoadingCache<String, Practitioner> practitionersMap = CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
          new CacheLoader<String, Practitioner>() {
            public Practitioner load(String practitionerId) {
              return read(practitionerId);
            }
          });

  /**
   * Builds a PractitionerClient
   *
   * @param client
   *    The FHIR client to use.
   */
  public PractitionerClient(IGenericClient client) {
    super(client);
  }

  private Practitioner read(String practitionerId) {
    return client.read()
        .resource(Practitioner.class)
        .withId(practitionerId)
        .execute();
  }

  /**
   * Returns a practitioner for a given ID.
   *
   * @param practitionerId
   *    The practitioner resource ID
   * @return
   *    A practitioner instance.
   */
  public Practitioner getPractitioner(String practitionerId) {
    return practitionersMap.getUnchecked(practitionerId);
  }

  /**
   * Invalidates cache entry for a practitioner.
   *
   * @param practitionerId
   *     practitioner ID
   */
  public void invalidate(String practitionerId) {
    practitionersMap.invalidate(practitionerId);
  }
}
