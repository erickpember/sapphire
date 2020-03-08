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
import ca.uhn.fhir.model.dstu2.resource.QuestionnaireResponse;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import java.util.List;

/**
 * Client utilities for QuestionnaireResponses.
 */
public class QuestionnaireResponseClient extends BaseClient<QuestionnaireResponse> {
  /**
   * Constructs an API client
   *
   * @param client The FHIR client to use.
   */
  public QuestionnaireResponseClient(IGenericClient client) {
    super(client);
  }

  /**
   * Returns a QuestionnaireResponse for a given order ID.
   *
   * @param questionnaireResponseId The questionnaire response ID.
   * @param encounterId ID of the parent Encounter resource.
   * @return A QuestionnaireResponse
   */
  public QuestionnaireResponse read(String questionnaireResponseId, String encounterId) {
    Bundle results = client.search().forResource(QuestionnaireResponse.class)
        .where(new StringClientParam(QuestionnaireResponse.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .where(new StringClientParam(QuestionnaireResponse.SP_RES_ID)
            .matches()
            .value(questionnaireResponseId))
        .returnBundle(Bundle.class)
        .execute();
    List<QuestionnaireResponse> resources = extractBundle(results, QuestionnaireResponse.class);
    if (!resources.isEmpty()) {
      return resources.get(0);
    } else {
      return null;
    }
  }

  /**
   * Creates a given QuestionnaireResponse.
   *
   * @param questionnaireResponse The QuestionnaireResponse to create.
   * @return The QuestionnaireResponse with populated native ID.
   */
  public QuestionnaireResponse create(QuestionnaireResponse questionnaireResponse) {
    MethodOutcome outcome = client.create().resource(questionnaireResponse).execute();
    questionnaireResponse.setId(outcome.getId());
    return questionnaireResponse;
  }

  /**
   * Updates a QuestionnaireResponse.
   *
   * @param questionnaireResponse The QuestionnaireResponse to update.
   */
  public void update(QuestionnaireResponse questionnaireResponse) {
    client.update().resource(questionnaireResponse).execute();
  }

  /**
   * Searches QuestionnaireResponses
   *
   * @param encounterId
   *     The ID of the encounter to which the questionnaire responses belong.
   * @return
   *     Medication orders for this encounter and status
   */
  public List<QuestionnaireResponse> search(String encounterId) {
    Bundle results = client.search().forResource(QuestionnaireResponse.class)
        .where(new StringClientParam(QuestionnaireResponse.SP_ENCOUNTER)
            .matches()
            .value(encounterId))
        .returnBundle(Bundle.class)
        .execute();

    List<QuestionnaireResponse> medicationOrders = extractBundle(results,
        QuestionnaireResponse.class);

    return medicationOrders;
  }
}
