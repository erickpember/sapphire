// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
