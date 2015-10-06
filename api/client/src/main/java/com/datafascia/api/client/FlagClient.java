// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.client;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

/**
 * Client utilities for Flag resources.
 */
public class FlagClient extends BaseClient<Flag> {
  /**
   * Builds a FlagClient
   *
   * @param client
   *    The FHIR client to use.
   */
  public FlagClient(IGenericClient client) {
    super(client);
  }

  /**
   * Save a given Flag.
   *
   * @param flag
   *    The Flag to save.
   * @return
   *    The Flag with populated native ID.
   */
  public Flag saveFlag(Flag flag) {
    MethodOutcome outcome = client.create().resource(flag).execute();
    flag.setId(outcome.getId());
    return flag;
  }

  /**
   * Updates a Flag.
   *
   * @param flag
   *    The Flag to update.
   */
  public void updateFlag(Flag flag) {
    client.update().resource(flag).execute();
  }

  /**
   * Searches Flags
   *
   * @param patientId
   *    The ID of the patient to which the flags belong.
   * @param code
   *    Code of flag, optional.
   * @param status
   *    Status of flag, optional.
   * @return
   *    A list of Flags.
   */
  public List<Flag> searchFlag(String patientId, String code,
      String status) {
    Bundle results = client.search().forResource(Flag.class)
        .where(new StringClientParam(Flag.SP_PATIENT)
            .matches()
            .value(patientId))
        .execute();

    List<Flag> flags = extractBundle(results, Flag.class);

    if (!Strings.isNullOrEmpty(code)) {
      List<Flag> filteredResults = new ArrayList<>();
      for (Flag flag : flags) {
        if (flag.getCode().getCodingFirstRep().getCode().equalsIgnoreCase(code)) {
          filteredResults.add(flag);
        }
      }
      flags = filteredResults;
    }

    if (!Strings.isNullOrEmpty(status)) {
      List<Flag> filteredResults = new ArrayList<>();
      for (Flag flag : flags) {
        if (flag.getStatus().equalsIgnoreCase(status)) {
          filteredResults.add(flag);
        }
      }
      flags = filteredResults;
    }

    return flags;
  }
}