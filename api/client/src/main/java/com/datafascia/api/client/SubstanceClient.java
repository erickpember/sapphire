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

import ca.uhn.fhir.model.dstu2.resource.Substance;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Client utilities for substances.
 */
public class SubstanceClient extends BaseClient<Substance> {
  /**
   * Builds a SubstanceClient
   *
   * @param client
   *    The FHIR client to use.
   */
  public SubstanceClient(IGenericClient client) {
    super(client);
  }

  /**
   * Save a given substance.
   *
   * @param substance
   *  The substance to save.
   * @return The substance with populated native ID.
   */
  public Substance saveSubstance(Substance substance) {
    MethodOutcome outcome = client.create().resource(substance).execute();
    substance.setId(outcome.getId());
    return substance;
  }

  /**
   * Returns a substance for a given id.
   *
   * @param id
   *    The substance resource ID
   * @return
   *    A substance instance.
   */
  public Substance getSubstance(String id) {
    return client.read()
        .resource(Substance.class)
        .withId(id)
        .execute();
  }
}
