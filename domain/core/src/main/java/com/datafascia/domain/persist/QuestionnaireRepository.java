// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Questionnaire;
import ca.uhn.fhir.model.primitive.IdDt;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Questionnaire data access.
 */
@Slf4j
public class QuestionnaireRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore entity store
   */
  @Inject
  public QuestionnaireRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts questionnaire ID to entity ID.
   *
   * @param questionnaireId questionnaire ID
   * @return entity ID
   */
  public static EntityId toEntityId(Id<Questionnaire> questionnaireId) {
    return new EntityId(Questionnaire.class, questionnaireId);
  }

  /**
   * Generates primary key from institution questionnaire identifier.
   *
   * @param questionnaire questionnaire to read property from
   * @return primary key
   */
  public static Id<Questionnaire> generateId(Questionnaire questionnaire) {
    String identifierValue = questionnaire.getIdentifierFirstRep().getValue();
    return Id.of(identifierValue);
  }

  /**
   * Saves entity.
   *
   * @param questionnaire to save
   */
  public void save(Questionnaire questionnaire) {
    Id<Questionnaire> questionnaireId = generateId(questionnaire);
    log.info("Setting questionnaire " + questionnaire + " with id " + questionnaireId);
    questionnaire.setId(new IdDt(Questionnaire.class.getSimpleName(), questionnaireId.toString()));

    entityStore.save(toEntityId(questionnaireId), questionnaire);
  }

  /**
   * Reads questionnaire.
   *
   * @param questionnaireId questionnaire ID
   * @return optional entity, empty if not found
   */
  public Optional<Questionnaire> read(Id<Questionnaire> questionnaireId) {
    return entityStore.read(toEntityId(questionnaireId));
  }

  /**
   * Finds all questionnaires, or a set filtered by status.
   *
   * @return questionnaires
   */
  public List<Questionnaire> list() {
    Stream<Questionnaire> stream = entityStore.stream(Questionnaire.class);

    return stream.collect(Collectors.toList());
  }

  /**
   * Deletes questionnaire and all of its children.
   *
   * @param questionnaireId questionnaire ID
   */
  public void delete(Id<Questionnaire> questionnaireId) {
    entityStore.delete(toEntityId(questionnaireId));
  }
}
