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

import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;

/**
 * Client utilities for medications.
 */
public class MedicationClient extends BaseClient<Medication> {
  private final LoadingCache<String, Medication> medicationsMap = CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES)
      .build(
          new CacheLoader<String, Medication>() {
            public Medication load(String medicationId) {
              return read(medicationId);
            }
          });

  /**
   * Builds a MedicationClient
   *
   * @param client The FHIR client to use.
   */
  public MedicationClient(IGenericClient client) {
    super(client);
  }

  private Medication read(String medicationId) {
    return client.read()
        .resource(Medication.class)
        .withId(medicationId)
        .execute();
  }

  /**
   * Retrieve a medication for a given FHIR-native ID.
   *
   * @param nativeId The FHIR-native ID to fetch for.
   * @return A medication.
   */
  public Medication getMedication(String nativeId) {
    Medication result = null;

    try {
      result = medicationsMap.getUnchecked(nativeId);
    } catch (Exception e) {
    }
    return result;
  }

  /**
   * Saves a medication.
   * @param medication The medication to save.
   * @return The resulting ID of the medication.
   */
  public Medication saveMedication(Medication medication) {
    MethodOutcome outcome = client.create().resource(medication).execute();
    medication.setId(outcome.getId());
    return medication;
  }

  /**
   * Invalidates cache entry for a medication.
   *
   * @param medicationId
   *     medication ID
   */
  public void invalidate(String medicationId) {
    medicationsMap.invalidate(medicationId);
  }
}
