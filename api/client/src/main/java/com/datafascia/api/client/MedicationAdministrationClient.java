// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import java.util.List;

/**
 * Client utilities for medication administrations.
 */
public class MedicationAdministrationClient {
  private final IGenericClient client;

  /**
   * Builds a MedicationAdministrationClient
   *
   * @param client The FHIR client to use.
   */
  public MedicationAdministrationClient(IGenericClient client) {
    this.client = client;
  }

  /**
   * Returns a MedicationAdministration for a given order ID and admin ID.
   *
   * @param adminId The medication administration ID.
   * @param encounterId The ID of the parent encounter.
   * @param prescriptionId The prescription ID.
   * @return A MedicationAdministrationClient
   */
  public MedicationAdministration getMedicationAdministration(String adminId,
      String encounterId, String prescriptionId) {
    Bundle results = client.search().forResource(MedicationAdministration.class)
        .where(new StringClientParam(MedicationAdministration.SP_RES_ID)
            .matches()
            .value(prescriptionId + "-" + adminId))
        .where(new StringClientParam(MedicationAdministration.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .where(new StringClientParam(MedicationAdministration.SP_PRESCRIPTION)
            .matches()
            .value(prescriptionId))
        .execute();
    List<BundleEntry> entries = results.getEntries();
    if (!entries.isEmpty()) {
      return (MedicationAdministration) entries.get(0).getResource();
    } else {
      return null;
    }
  }

  /**
   * Returns a MedicationAdministration for a given identifier.
   *
   * @param adminId The resource ID.
   * @param encounterId The ID of the parent encounter.
   * @return A MedicationAdministration
   */
  public MedicationAdministration getMedicationAdministration(String adminId,
      String encounterId) {
    Bundle results = client.search().forResource(MedicationAdministration.class)
        .where(new StringClientParam(MedicationAdministration.SP_RES_ID)
            .matches()
            .value(adminId))
        .where(new StringClientParam(MedicationAdministration.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();
    List<BundleEntry> entries = results.getEntries();
    if (!entries.isEmpty()) {
      return (MedicationAdministration) entries.get(0).getResource();
    } else {
      return null;
    }
  }

  /**
   * Save a given MedicationAdministration.
   *
   * @param administration The MedicationAdministration to save.
   * @return The MedicationAdministration with populated native ID.
   */
  public MedicationAdministration saveAdministration(MedicationAdministration administration) {
    MethodOutcome outcome = client.create().resource(administration).execute();
    administration.setId(outcome.getId());
    return administration;
  }

  /**
   * Updates a MedicationAdministration.
   *
   * @param admin The MedicationAdministration to update.
   */
  public void updateMedicationAdministration(MedicationAdministration admin) {
    client.update().resource(admin).execute();
  }
}
