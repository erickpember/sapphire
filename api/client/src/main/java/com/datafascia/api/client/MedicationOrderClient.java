// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
   * Searches MedicationOrders
   *
   * @param encounterId
   *     The ID of the encounter to which the medication orders belong.
   * @return
   *     Medication orders for this encounter
   */
  public List<MedicationOrder> search(String encounterId) {
    return search(encounterId, null);
  }

  /**
   * Searches MedicationOrders
   *
   * @param encounterId
   *     The ID of the encounter to which the medicationOrders belong.
   * @param status
   *     Status of medicationOrder, optional.
   * @return
   *     Medication orders for this encounter and status
   */
  public List<MedicationOrder> search(String encounterId, String status) {
    Bundle results = client.search().forResource(MedicationOrder.class)
        .where(new StringClientParam(MedicationOrder.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();

    List<MedicationOrder> medicationOrders = extractBundle(results, MedicationOrder.class);

    if (!Strings.isNullOrEmpty(status)) {
      List<MedicationOrder> filteredResults = new ArrayList<>();
      for (MedicationOrder medicationOrder : medicationOrders) {
        if (medicationOrder.getStatus().equalsIgnoreCase(status)) {
          filteredResults.add(medicationOrder);
        }
      }
      medicationOrders = filteredResults;
    }

    return medicationOrders;
  }

  /**
   * Searches MedicationOrders
   *
   * @param encounterId
   *     The ID of the encounter to which the medicationOrders belong.
   * @param status
   *     Status of medicationOrder, optional.
   * @param identifier
   *     Medication identifier, AKA MedsSet, optional.
   * @return
   *     Medication orders for this encounter and status
   */
  public List<MedicationOrder> search(String encounterId, String status, String identifier) {
    if (identifier == null) {
      return search(encounterId, status);
    } else {
      return search(encounterId, status).stream().filter(order -> order.getIdentifierFirstRep()
          .getValue().equals(identifier)).collect(Collectors.toList());
    }
  }
}
