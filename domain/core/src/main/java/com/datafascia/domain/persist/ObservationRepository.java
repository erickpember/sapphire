// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.domain.fhir.Ids;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Observation data access.
 */
@Slf4j
public class ObservationRepository extends FhirEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public ObservationRepository(FhirEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(Id<Encounter> encounterId, Id<Observation> observationId) {
    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(Observation.class, observationId)
        .build();
  }

  /**
   * Generates primary key from observation resource ID.
   *
   * @param observation
   *      observation to read property from
   * @return primary key
   * @throws IllegalArgumentException if observation does not have a resource ID
   */
  public static Id<Observation> generateId(Observation observation) {
    if (observation.getId().isEmpty()) {
      throw new IllegalArgumentException("Observation missing id");
    }

    return Id.of(observation.getId().getIdPart());
  }

  /**
   * Saves entity.
   *
   * @param encounter
   *     parent entity
   * @param observation
   *     to save
   */
  public void save(Encounter encounter, Observation observation) {
    Id<Observation> observationId = generateId(observation);
    Id<Encounter> encounterId = Ids.toPrimaryKey(encounter.getId());
    entityStore.save(toEntityId(encounterId, observationId), observation);
  }

  /**
   * Saves entity.
   *
   * @param observation to save
   */
  public void save(Observation observation) {
    Id<Observation> observationId = generateId(observation);
    Id<Encounter> encounterId = Ids.toPrimaryKey(observation.getEncounter().getReference());
    entityStore.save(toEntityId(encounterId, observationId), observation);
  }

  /**
   * Finds observations for an encounter.
   *
   * @param encounterId
   *     encounter ID
   * @return observations
   */
  public List<Observation> list(Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(encounterId), Observation.class)
        .collect(Collectors.toList());
  }

  /**
   * Deletes encounter and all of its children.
   *
   * @param encounterId
   *     parent encounter ID
   * @param observationId
   *     observation ID to delete
   */
  public void delete(Id<Encounter> encounterId, Id<Observation> observationId) {
    entityStore.delete(toEntityId(encounterId, observationId));
  }
}
