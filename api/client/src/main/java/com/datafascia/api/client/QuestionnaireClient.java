// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
