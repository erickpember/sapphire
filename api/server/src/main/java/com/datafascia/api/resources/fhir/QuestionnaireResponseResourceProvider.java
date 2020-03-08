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
package com.datafascia.api.resources.fhir;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.QuestionnaireResponse;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.fhir.Ids;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.domain.persist.QuestionnaireResponseRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * QuestionnaireResponseResourceProvider resource endpoint
 */
@Slf4j
public class QuestionnaireResponseResourceProvider implements IResourceProvider {

  @Inject
  private EncounterRepository encounterRepository;

  @Inject
  private QuestionnaireResponseRepository questionnaireResponseRepository;

  /**
   * The getResourceType method comes from IResourceProvider, and must be overridden to indicate
   * what type of resource this provider supplies.
   *
   * @return Class of resource.
   */
  @Override
  public Class<QuestionnaireResponse> getResourceType() {
    return QuestionnaireResponse.class;
  }

  /**
   * Store a new QuestionnaireResponse.
   *
   * @param questionnaireResponse The new QuestionnaireResponse to store.
   * @return Outcome of create method. Resource ID of QuestionnaireResponse.
   */
  @Create
  public MethodOutcome create(@ResourceParam QuestionnaireResponse questionnaireResponse) {
    if (questionnaireResponse.getEncounter() == null) {
      throw new UnprocessableEntityException("Can not create QuestionnaireResponse:"
          + " encounter reference can not be null.");
    }

    questionnaireResponseRepository.save(questionnaireResponse);
    return new MethodOutcome(questionnaireResponse.getId());
  }

  /**
   * Because the QuestionnaireResponseRepository does not support single-argument reads, a
   * double-argument read method that requires the Encounter ID as well as the questionnaire
   * response ID is implemented here in the API as a search method. Absent a
   * questionnaireResponseId, all questionnaire responses are searched.
   *
   * @param encounterId
   *    Internal resource ID of the specific Encounter to search.
   * @param questionnaireResponseId
   *    Resource ID of the specific QuestionnaireResponse we want to retrieve.
   * @return QuestionnaireResponse list, matching this query.
   */
  @Search()
  public List<QuestionnaireResponse> search(
      @RequiredParam(name = QuestionnaireResponse.SP_ENCOUNTER) StringParam encounterId,
      @OptionalParam(name = QuestionnaireResponse.SP_RES_ID) StringParam questionnaireResponseId) {

    List<QuestionnaireResponse> questionnaireResponses = new ArrayList<>();

    if (questionnaireResponseId != null) {
      Id<Encounter> encounterInternalId = Id.of(encounterId.getValue());
      Id<QuestionnaireResponse> questionnaireResponseInternalId
          = Id.of(questionnaireResponseId.getValue());
      Optional<QuestionnaireResponse> result = questionnaireResponseRepository.read(
          encounterInternalId, questionnaireResponseInternalId);

      if (result.isPresent()) {
        questionnaireResponses.add(result.get());
      }
    } else {
      // Pull records for the encounter.
      questionnaireResponses.addAll(questionnaireResponseRepository.list(
          Id.of(encounterId.getValue())));
    }

    return questionnaireResponses;
  }

  /**
   * Completely replaces the content of the QuestionnaireResponse resource with the content given
   * in the request.
   *
   * @param questionnaireResponse New QuestionnaireResponse content.
   * @return Outcome of create method. Resource ID of QuestionnaireResponse.
   */
  @Update
  public MethodOutcome update(@ResourceParam QuestionnaireResponse questionnaireResponse) {
    IdDt resourceId = questionnaireResponse.getId();
    if (resourceId == null) {
      throw new UnprocessableEntityException("QuestionnaireResponse: no ID supplied. "
          + "Can not update.");
    }

    checkForEncounterReference(questionnaireResponse);

    Id<Encounter> encounterId = Ids.toPrimaryKey(
        questionnaireResponse.getEncounter().
            getReference());

    // Check if entity already exists.
    Id<QuestionnaireResponse> medicationPrescriptionId
        = QuestionnaireResponseRepository.generateId(questionnaireResponse);
    Optional<QuestionnaireResponse> optionalQuestionnaireResponse
        = questionnaireResponseRepository.read(
        encounterId, medicationPrescriptionId);
    if (!optionalQuestionnaireResponse.isPresent()) {
      throw new InvalidRequestException(String.format(
          "QuestionnaireResponse ID [%s] did not already exist",
          medicationPrescriptionId));
    }

    questionnaireResponseRepository.save(questionnaireResponse);
    return new MethodOutcome(questionnaireResponse.getId());
  }

  private void checkForEncounterReference(QuestionnaireResponse questionnaireResponse)
      throws UnprocessableEntityException {

    if (questionnaireResponse.getEncounter() == null
        || questionnaireResponse.getEncounter().isEmpty()) {
      throw new UnprocessableEntityException("QuestionnaireResponse with identifier "
          + questionnaireResponse.getIdentifier().getValue()
          + " lacks the mandatory reference to encounter, can not be saved or updated.");
    }
  }
}
