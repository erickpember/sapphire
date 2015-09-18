// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.resource.MedicationPrescription;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import java.util.List;

/**
 * Client utilities for medication prescriptions.
 */
public class MedicationPrescriptionClient {
  private final IGenericClient client;

  /**
   * Builds a MedicationPrescriptionClient
   *
   * @param client The FHIR client to use.
   */
  public MedicationPrescriptionClient(IGenericClient client) {
    this.client = client;
  }

  /**
   * Returns a MedicationPrescription for a given order ID.
   *
   * @param prescriptionId The prescription ID.
   * @param encounterId ID of the parent Encounter resource.
   * @return A MedicationPrescription
   */
  public MedicationPrescription getMedicationPrescription(String prescriptionId,
      String encounterId) {
    Bundle results = client.search().forResource(MedicationPrescription.class)
        .where(new StringClientParam(MedicationPrescription.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .where(new StringClientParam(MedicationPrescription.SP_RES_ID)
            .matches()
            .value(prescriptionId))
        .execute();
    List<BundleEntry> entries = results.getEntries();
    if (!entries.isEmpty()) {
      return (MedicationPrescription) entries.get(0).getResource();
    } else {
      return null;
    }
  }

  /**
   * Save a given MedicationPrescription.
   *
   * @param prescription The MedicationPrescription to save.
   * @return The MedicationPrescription with populated native ID.
   */
  public MedicationPrescription savePrescription(MedicationPrescription prescription) {
    MethodOutcome outcome = client.create().resource(prescription).execute();
    prescription.setId(outcome.getId());
    return prescription;
  }

  /**
   * Updates a MedicationPrescription.
   *
   * @param prescription The MedicationPrescription to update.
   */
  public void updateMedicationPrescription(MedicationPrescription prescription) {
    client.update().resource(prescription).execute();
  }

  /**
   * Fetches a list of MedicationPrescription resources
   *
   * @param encounterId
   *     the encounter identifier linked to the MedicationPrescription resources
   * @return medicationPrescriptions
   *     a list MedicationPrescriptions
   */
  public List<MedicationPrescription> getMedicationPrescriptions(String encounterId) {
    Bundle bundle = client.search().forResource(MedicationPrescription.class)
        .where(new StringClientParam(MedicationPrescription.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();

    List<MedicationPrescription> prescriptions =
        bundle.getResources(MedicationPrescription.class);
    while (bundle.getLinkNext().isEmpty() == false) {
      bundle = client.loadPage().next(bundle).execute();
      prescriptions.addAll(bundle.getResources(MedicationPrescription.class));
    }

    return prescriptions;
  }
}
