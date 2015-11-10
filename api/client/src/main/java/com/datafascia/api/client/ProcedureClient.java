// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

/**
 * Client utilities for procedures.
 */
public class ProcedureClient extends BaseClient<Procedure> {
  /**
   * Builds a ProcedureClient
   *
   * @param client The FHIR client to use.
   */
  public ProcedureClient(IGenericClient client) {
    super(client);
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
   * Searches Procedure
   *
   * @param encounterId The ID of the encounter to which the procedure belong.
   * @param code Code of procedure, optional.
   * @param status Status of procedure, optional.
   * @return A list Procedures.
   */
  public List<Procedure> searchProcedure(String encounterId, String code,
      String status) {
    Bundle results = client.search().forResource(Procedure.class)
        .where(new StringClientParam(Procedure.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .execute();

    List<Procedure> procedures = extractBundle(results, Procedure.class);

    if (!Strings.isNullOrEmpty(code)) {
      List<Procedure> filteredResults = new ArrayList<>();
      for (Procedure procedure : procedures) {
        if (procedure.getCode().getCodingFirstRep().getCode().equalsIgnoreCase(code)) {
          filteredResults.add(procedure);
        }
      }
      procedures = filteredResults;
    }

    if (!Strings.isNullOrEmpty(status)) {
      List<Procedure> filteredResults = new ArrayList<>();
      for (Procedure procedure : procedures) {
        if (procedure.getStatus().equalsIgnoreCase(status)) {
          filteredResults.add(procedure);
        }
      }
      procedures = filteredResults;
    }

    return procedures;
  }
}
