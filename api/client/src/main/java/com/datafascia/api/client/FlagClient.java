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
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Client utilities for Flag resources.
 */
@Slf4j
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
    Bundle results = null;
    try {
      results = client.search().forResource(Flag.class)
          .where(new StringClientParam(Flag.SP_PATIENT)
              .matches()
              .value(patientId))
          .returnBundle(Bundle.class)
          .execute();
    } catch (RuntimeException e) {
      log.error("FlagClient search by patient failed. patientId:[{}]", patientId);
      return new ArrayList<>();
    }

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
