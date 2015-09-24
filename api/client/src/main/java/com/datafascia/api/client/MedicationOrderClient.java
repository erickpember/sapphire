// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import java.util.List;

/**
 * Client utilities for medication orders.
 */
public class MedicationOrderClient extends BaseClient<MedicationOrder> {
  /**
   * Constructs an API client
   *
   * @param client The FHIR client to use.
   */
  public MedicationOrderClient(IGenericClient client) {
    super(client);
  }

  /**
   * Returns a MedicationOrder for a given order ID.
   *
   * @param orderId The order ID.
   * @param encounterId ID of the parent Encounter resource.
   * @return A MedicationOrder
   */
  public MedicationOrder read(String orderId, String encounterId) {
    Bundle results = client.search().forResource(MedicationOrder.class)
        .where(new StringClientParam(MedicationOrder.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .where(new StringClientParam(MedicationOrder.SP_RES_ID)
            .matches()
            .value(orderId))
        .execute();
    List<BundleEntry> entries = results.getEntries();
    if (!entries.isEmpty()) {
      return (MedicationOrder) entries.get(0).getResource();
    } else {
      return null;
    }
  }

  /**
   * Creates a given MedicationOrder.
   *
   * @param order The MedicationOrder to create.
   * @return The MedicationOrder with populated native ID.
   */
  public MedicationOrder create(MedicationOrder order) {
    MethodOutcome outcome = client.create().resource(order).execute();
    order.setId(outcome.getId());
    return order;
  }

  /**
   * Updates a MedicationOrder.
   *
   * @param order The MedicationOrder to update.
   */
  public void update(MedicationOrder order) {
    client.update().resource(order).execute();
  }

  /**
   * Fetches a list of MedicationOrder resources
   *
   * @param encounterId
   *     the encounter identifier linked to the MedicationOrder resources
   * @return MedicationOrder list
   */
  public List<MedicationOrder> list(String encounterId) {
    Bundle bundle = client.search().forResource(MedicationOrder.class)
        .where(new StringClientParam(MedicationOrder.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();

    List<MedicationOrder> prescriptions =
        bundle.getResources(MedicationOrder.class);
    while (bundle.getLinkNext().isEmpty() == false) {
      bundle = client.loadPage().next(bundle).execute();
      prescriptions.addAll(bundle.getResources(MedicationOrder.class));
    }

    return prescriptions;
  }
}
