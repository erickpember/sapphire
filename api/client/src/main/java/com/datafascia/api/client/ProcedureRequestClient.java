// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import java.util.List;

/**
 * Client utilities for procedure requests.
 */
public class ProcedureRequestClient extends BaseClient<ProcedureRequest> {
  /**
   * Builds a ProcedureRequestClient
   *
   * @param client The FHIR client to use.
   */
  public ProcedureRequestClient(IGenericClient client) {
    super(client);
  }

  /**
   * Updates a ProcedureRequest
   *
   * @param procedure The ProcedureRequest to update.
   * @param encounterId The Encounter the request belongs to.
   */
  public void updateProcedureRequest(ProcedureRequest procedure, String encounterId) {
    // Make sure the order already exists.
    ProcedureRequest existingRequest = getProcedureRequest(procedure.getIdentifierFirstRep()
        .getValue(), encounterId);
    procedure.setId(existingRequest.getId());
    client.update().resource(procedure).execute();
  }

  /**
   * Saves a ProcedureRequest
   *
   * @param procedure The ProcedureRequest to save.
   * @param encounterId The Encounter the request belongs to.
   * @return A ProcedureRequest.
   */
  public ProcedureRequest saveProcedureRequest(ProcedureRequest procedure, String encounterId) {
    MethodOutcome outcome = client.create().resource(procedure).execute();
    procedure.setId(outcome.getId());
    return procedure;
  }

  /**
   * Fetches a ProcedureRequest
   *
   * @param requestId The ID of the request.
   * @param encounterId The ID of the encounter it belongs to.
   * @return A ProcedureRequest.
   */
  public ProcedureRequest getProcedureRequest(String requestId, String encounterId) {
    Bundle results = client.search().forResource(ProcedureRequest.class)
        .where(new StringClientParam(ProcedureRequest.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .where(new StringClientParam(ProcedureRequest.SP_RES_ID)
            .matches()
            .value(requestId))
        .execute();
    List<BundleEntry> entries = results.getEntries();
    if (!entries.isEmpty()) {
      return (ProcedureRequest) entries.get(0).getResource();
    } else {
      return null;
    }
  }
}
