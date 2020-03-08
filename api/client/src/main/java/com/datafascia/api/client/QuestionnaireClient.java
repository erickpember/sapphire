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

import ca.uhn.fhir.model.dstu2.resource.Questionnaire;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

/**
 * Client utilities for Questionnaires.
 */
public class QuestionnaireClient extends BaseClient<Questionnaire> {
  /**
   * Builds a QuestionnaireClient
   *
   * @param client The FHIR client to use.
   */
  public QuestionnaireClient(IGenericClient client) {
    super(client);
  }

  /**
   * Retrieve a questionnaire for a given FHIR-native ID.
   *
   * @param nativeId The FHIR-native ID to fetch for.
   * @return A questionnaire.
   */
  public Questionnaire getQuestionnaire(String nativeId) {
    try {
      return client.read()
          .resource(Questionnaire.class)
          .withId(nativeId)
          .execute();
    } catch (ResourceNotFoundException e) {
      return null;
    }
  }

  /**
   * Saves a questionnaire.
   * @param questionnaire The questionnaire to save.
   * @return The resulting ID of the questionnaire.
   */
  public Questionnaire saveQuestionnaire(
      Questionnaire questionnaire) {
    MethodOutcome outcome = client.create().resource(questionnaire).execute();
    questionnaire.setId(outcome.getId());
    return questionnaire;
  }
}
