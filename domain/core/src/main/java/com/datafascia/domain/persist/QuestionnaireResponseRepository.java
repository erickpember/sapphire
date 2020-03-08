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
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.QuestionnaireResponse;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.domain.fhir.Ids;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * QuestionnaireResponse data access.
 */
@Slf4j
public class QuestionnaireResponseRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public QuestionnaireResponseRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(
      Id<Encounter> encounterId, Id<QuestionnaireResponse> prescriptionId) {

    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(QuestionnaireResponse.class, prescriptionId)
        .build();
  }

  /**
   * Generates primary key from institution-assigned questionnaire response identifier.
   *
   * @param questionnaireResponse medication prescription from which to read the identifier
   * @return primary key
   */
  public static Id<QuestionnaireResponse> generateId(QuestionnaireResponse questionnaireResponse) {
    String identifierValue = questionnaireResponse.getIdentifier().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param encounter parent entity
   * @param questionnaireResponse to save
   */
  public void save(Encounter encounter, QuestionnaireResponse questionnaireResponse) {
    Id<QuestionnaireResponse> questionnaireResponseId = generateId(questionnaireResponse);
    questionnaireResponse.setId(new IdDt(
        QuestionnaireResponse.class.getSimpleName(), questionnaireResponseId.toString()));

    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    entityStore.save(toEntityId(encounterId, questionnaireResponseId), questionnaireResponse);
  }

  /**
   * Saves entity.
   *
   * @param encounterId  parent entity ID
   * @param questionnaireResponse to save
   */
  public void save(Id<Encounter> encounterId, QuestionnaireResponse questionnaireResponse) {
    Id<QuestionnaireResponse> questionnaireResponseId = generateId(questionnaireResponse);
    questionnaireResponse.setId(new IdDt(
        QuestionnaireResponse.class.getSimpleName(), questionnaireResponseId.toString()));

    entityStore.save(toEntityId(encounterId, questionnaireResponseId), questionnaireResponse);
  }

  /**
   * Saves entity.
   *
   * @param questionnaireResponse to save
   */
  public void save(QuestionnaireResponse questionnaireResponse) {
    Id<QuestionnaireResponse> questionnaireResponseId = generateId(questionnaireResponse);
    questionnaireResponse.setId(new IdDt(
        QuestionnaireResponse.class.getSimpleName(), questionnaireResponseId.toString()));

    Id<Encounter> encounterId
        = Ids.toPrimaryKey(questionnaireResponse.getEncounter().getReference());

    entityStore.save(toEntityId(encounterId, questionnaireResponseId), questionnaireResponse);
  }

  /**
   * Reads entity.
   *
   * @param encounterId      parent entity ID
   * @param questionnaireResponseId to read
   * @return Optional entity, empty if not found.
   */
  public Optional<QuestionnaireResponse> read(
      Id<Encounter> encounterId, Id<QuestionnaireResponse> questionnaireResponseId) {

    return entityStore.read(toEntityId(encounterId, questionnaireResponseId));
  }

  /**
   * Finds questionnaire response for an encounter.
   *
   * @param encounterId encounter ID
   * @return medication questionnaire responses
   */
  public List<QuestionnaireResponse> list(Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(encounterId), QuestionnaireResponse.class)
        .collect(Collectors.toList());
  }
}
