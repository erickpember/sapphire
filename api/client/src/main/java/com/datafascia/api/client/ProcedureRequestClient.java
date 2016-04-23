// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Client utilities for procedure requests.
 */
public class ProcedureRequestClient extends BaseClient<ProcedureRequest> {
  private final LoadingCache<String, List<ProcedureRequest>> encounterIdToProcedureRequestsMap
      = CacheBuilder.newBuilder()
      .expireAfterWrite(30, TimeUnit.SECONDS)
      .build(
          new CacheLoader<String, List<ProcedureRequest>>() {
            @Override
            public List<ProcedureRequest> load(String encounterId) {
              return list(encounterId);
            }
          });

  /**
   * Builds a ProcedureRequestClient
   *
   * @param client The FHIR client to use.
   */
  public ProcedureRequestClient(IGenericClient client) {
    super(client);
  }

  private List<ProcedureRequest> list(String encounterId) {
    Bundle results = client.search()
        .forResource(ProcedureRequest.class)
        .where(new StringClientParam(ProcedureRequest.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .returnBundle(Bundle.class)
        .execute();

    return extractBundle(results, ProcedureRequest.class);
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
        .returnBundle(Bundle.class)
        .execute();
    return extractBundle(results, ProcedureRequest.class)
        .stream()
        .findFirst();
  }

  /**
   * Lists all procedure requests for a given encounter.
   *
   * @param encounterId
   *     The ID of the encounter to which the procedure requests belong.
   * @return A list ProcedureRequests.
   */
  public List<ProcedureRequest> search(String encounterId) {
    return search(encounterId, null, null);
  }

  /**
   * Searches ProcedureRequests
   *
   * @param encounterId
   *     The ID of the encounter to which the procedure requests belong. Not optional.
   * @param code
   *     procedure request code, optional.
   * @param status
   *     Status of procedure request, optional.
   * @return A list ProcedureRequests.
   */
  public List<ProcedureRequest> search(String encounterId, String code, String status) {
    List<ProcedureRequest> procedurerequests = encounterIdToProcedureRequestsMap.getUnchecked(
        encounterId);
    if (Strings.isNullOrEmpty(code) && Strings.isNullOrEmpty(status)) {
      return procedurerequests;
    }

    return procedurerequests.stream()
        .filter(procedurerequest -> Strings.isNullOrEmpty(code) ||
            code.equalsIgnoreCase(procedurerequest.getCode().getCodingFirstRep().getCode()))
        .filter(procedurerequest -> Strings.isNullOrEmpty(status) ||
            status.equalsIgnoreCase(procedurerequest.getStatus()))
        .collect(Collectors.toList());
  }

  /**
   * Invalidates cache entry for encounter-based search results.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidate(String encounterId) {
    encounterIdToProcedureRequestsMap.invalidate(encounterId);
  }
}
