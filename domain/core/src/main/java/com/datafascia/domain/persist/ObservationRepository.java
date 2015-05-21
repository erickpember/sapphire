// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Observation;
import com.datafascia.domain.model.Patient;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Observation data access.
 */
@Slf4j
public class ObservationRepository extends EntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public ObservationRepository(ReflectEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(
      Id<Patient> patientId, Id<Encounter> encounterId, Id<Observation> observationId) {

    return EntityId.builder()
        .path(EncounterRepository.toEntityId(patientId, encounterId))
        .path(Observation.class, observationId)
        .build();
  }

  private static Id<Observation> generateId(Observation observation) {
    return (observation.getId() != null)
        ? observation.getId()
        : Id.of(UUID.randomUUID().toString());
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     parent entity
   * @param encounter
   *     parent entity
   * @param observation
   *     to save
   */
  public void save(Patient patient, Encounter encounter, Observation observation) {
    observation.setId(generateId(observation));

    entityStore.save(
        toEntityId(patient.getId(), encounter.getId(), observation.getId()),
        observation);
  }

  /**
   * Finds observations for an encounter.
   *
   * @param patientId
   *     parent entity ID
   * @param encounterId
   *     encounter ID
   * @return observations
   */
  public List<Observation> list(Id<Patient> patientId, Id<Encounter> encounterId) {
    return entityStore
        .stream(EncounterRepository.toEntityId(patientId, encounterId), Observation.class)
        .collect(Collectors.toList());
  }
}
