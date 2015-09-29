// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

/**
 * Client utilities for medication administrations.
 */
public class MedicationAdministrationClient extends BaseClient<MedicationAdministration> {
  /**
   * Builds a MedicationAdministrationClient
   *
   * @param client The FHIR client to use.
   */
  public MedicationAdministrationClient(IGenericClient client) {
    super(client);
  }

  /**
   * Returns a list of administrations for a given encounter.
   *
   * @param encounterId
   *     ID of the parent Encounter resource.
   * @return
   *     Medication administrations for a given encounter.
   */
  public List<MedicationAdministration> search(String encounterId) {
    return search(encounterId, null);
  }

  /**
   * Searches medication administrations by encounter and status
   *
   * @param encounterId
   *     The ID of the encounter to which the medicationAdministrations belong.
   * @param status
   *    Status of medicationAdministration, optional.
   * @return
   *    A list of MedicationAdministrations for the encounter and status.
   */
  public List<MedicationAdministration> search(String encounterId, String status) {
    Bundle results = client.search().forResource(MedicationAdministration.class)
        .where(new StringClientParam(MedicationAdministration.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();

    List<MedicationAdministration> medicationAdministrations = extractBundle(results,
        MedicationAdministration.class);

    if (!Strings.isNullOrEmpty(status)) {
      List<MedicationAdministration> filteredResults = new ArrayList<>();
      for (MedicationAdministration medicationAdministration : medicationAdministrations) {
        if (medicationAdministration.getStatus().equalsIgnoreCase(status)) {
          filteredResults.add(medicationAdministration);
        }
      }
      medicationAdministrations = filteredResults;
    }

    return medicationAdministrations;
  }

  /**
   * Returns a MedicationAdministration for a given order ID and admin ID.
   *
   * @param adminId
   *    The medication administration ID.
   * @param encounterId
   *    The ID of the parent encounter.
   * @param prescriptionId
   *    The prescription ID.
   * @return
   *    A MedicationAdministrationClient
   */
  public MedicationAdministration get(String adminId, String encounterId, String prescriptionId) {
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
   * @param adminId
   *    The resource ID.
   * @param encounterId
   *    The ID of the parent encounter.
   * @return
   *    A medication administration for a given identifier.
   */
  public MedicationAdministration get(String adminId, String encounterId) {
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
   * @param administration
   *    The MedicationAdministration to save.
   * @return
   *    The MedicationAdministration with populated native ID.
   */
  public MedicationAdministration save(MedicationAdministration administration) {
    MethodOutcome outcome = client.create().resource(administration).execute();
    administration.setId(outcome.getId());
    return administration;
  }

  /**
   * Updates a MedicationAdministration.
   *
   * @param admin
   *    The MedicationAdministration to update.
   */
  public void update(MedicationAdministration admin) {
    client.update().resource(admin).execute();
  }
}
