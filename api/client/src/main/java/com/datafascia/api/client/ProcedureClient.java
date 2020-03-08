// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.api.client;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * Client utilities for procedures.
 */
public class ProcedureClient extends BaseClient<Procedure> {
  private final LoadingCache<String, List<Procedure>> encounterIdToProceduresMap = CacheBuilder
      .newBuilder()
      .expireAfterWrite(30, TimeUnit.SECONDS)
      .build(
          new CacheLoader<String, List<Procedure>>() {
            @Override
            public List<Procedure> load(String encounterId) {
              return list(encounterId);
            }
          });

  /**
   * Builds a ProcedureClient
   *
   * @param client The FHIR client to use.
   */
  public ProcedureClient(IGenericClient client) {
    super(client);
  }

  private List<Procedure> list(String encounterId) {
    Bundle results = client.search()
        .forResource(Procedure.class)
        .where(new StringClientParam(Procedure.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .returnBundle(Bundle.class)
        .execute();

    return extractBundle(results, Procedure.class);
  }

  /**
   * Save a given Procedure.
   *
   * @param procedure The Observation to save.
   * @return The Procedure with populated native ID.
   */
  public Procedure saveProcedure(Procedure procedure) {
    MethodOutcome outcome = client.create().resource(procedure).execute();
    procedure.setId(outcome.getId());
    return procedure;
  }

  /**
   * Updates a Procedure.
   *
   * @param procedure The Procedure to update.
   */
  public void updateProcedure(Procedure procedure) {
    client.update().resource(procedure).execute();
  }

  /**
   * Searches Procedures
   *
   * @param encounterId
   *     The ID of the encounter to which the procedures belong, not optional.
   * @param code
   *     Code of procedure, optional.
   * @param status
   *     Status of procedure, optional.
   * @return A list of Procedures.
   */
  public List<Procedure> searchProcedure(String encounterId, String code, String status) {
    List<Procedure> procedures = encounterIdToProceduresMap.getUnchecked(encounterId);
    return procedures.stream()
        .filter(procedure -> Strings.isNullOrEmpty(code) ||
            code.equalsIgnoreCase(procedure.getCode().getCodingFirstRep().getCode()))
        .filter(procedure -> Strings.isNullOrEmpty(status) ||
            status.equalsIgnoreCase(procedure.getStatus()))
        .collect(Collectors.toList());
  }

  /**
   * Invalidates cache entry for encounter-based search results.
   *
   * @param encounterId
   *     encounter ID
   */
  public void invalidate(String encounterId) {
    encounterIdToProceduresMap.invalidate(encounterId);
  }
}
