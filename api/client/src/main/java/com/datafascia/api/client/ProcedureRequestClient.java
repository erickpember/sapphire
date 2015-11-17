// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
   * @param request The ProcedureRequest to update.
   */
  public void update(ProcedureRequest request) {
    client.update().resource(request).execute();
  }

  /**
   * Creates a ProcedureRequest
   *
   * @param request The ProcedureRequest to create.
   * @return A ProcedureRequest.
   */
  public ProcedureRequest create(ProcedureRequest request) {
    MethodOutcome outcome = client.create().resource(request).execute();
    request.setId(outcome.getId());
    return request;
  }

  /**
   * Fetches a ProcedureRequest
   *
   * @param requestId The ID of the request.
   * @param encounterId The ID of the encounter it belongs to.
   * @return optional procedure request, empty if not found
   */
  public Optional<ProcedureRequest> read(String requestId, String encounterId) {
    Bundle results = client.search().forResource(ProcedureRequest.class)
        .where(new StringClientParam(ProcedureRequest.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .where(new StringClientParam(ProcedureRequest.SP_RES_ID)
            .matches()
            .value(requestId))
        .execute();
    return results.getEntries()
        .stream()
        .findFirst()
        .map(entry -> (ProcedureRequest) entry.getResource());
  }

  /**
   * Lists all procedure requests for an encounter.
   *
   * @param encounterId The ID of the encounter they belong to.
   * @return A list ProcedureRequests.
   */
  public List<ProcedureRequest> list(String encounterId) {
    Bundle results = client.search().forResource(ProcedureRequest.class)
        .where(new StringClientParam(ProcedureRequest.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();
    return extractBundle(results, ProcedureRequest.class);
  }

  /**
   * Searches ProcedureRequests
   *
   * @param encounterId The ID of the encounter to which the procedure requests belong.
   * @param code procedure request code, optional.
   * @param status Status of procedure request, optional.
   * @return A list ProcedureRequests.
   */
  public List<ProcedureRequest> search(String encounterId, String code, String status) {
    Bundle results = client.search().forResource(ProcedureRequest.class)
        .where(new StringClientParam(ProcedureRequest.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();

    List<ProcedureRequest> procedureRequests = extractBundle(results, ProcedureRequest.class);

    if (!Strings.isNullOrEmpty(code)) {
      List<ProcedureRequest> filteredResults = new ArrayList<>();
      for (ProcedureRequest procedureRequest : procedureRequests) {
        if (procedureRequest.getCode().getCodingFirstRep().getCode().equalsIgnoreCase(code)) {
          filteredResults.add(procedureRequest);
        }
      }
      procedureRequests = filteredResults;
    }

    if (!Strings.isNullOrEmpty(status)) {
      List<ProcedureRequest> filteredResults = new ArrayList<>();
      for (ProcedureRequest procedureRequest : procedureRequests) {
        if (procedureRequest.getStatus().equalsIgnoreCase(status)) {
          filteredResults.add(procedureRequest);
        }
      }
      procedureRequests = filteredResults;
    }

    return procedureRequests;
  }
}
