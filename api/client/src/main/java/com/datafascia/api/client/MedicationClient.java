// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

/**
 * Client utilities for medications.
 */
public class MedicationClient extends BaseClient<Medication> {
  /**
   * Builds a MedicationClient
   *
   * @param client The FHIR client to use.
   */
  public MedicationClient(IGenericClient client) {
    super(client);
  }

  /**
   * Retrieve a medication for a given FHIR-native ID.
   *
   * @param nativeId The FHIR-native ID to fetch for.
   * @return A medication.
   */
  public Medication getMedication(String nativeId) {
    try {
      return client.read()
          .resource(Medication.class)
          .withId(nativeId)
          .execute();
    } catch (ResourceNotFoundException e) {
      return null;
    }
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
}
