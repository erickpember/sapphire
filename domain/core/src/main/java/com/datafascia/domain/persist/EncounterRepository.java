// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Patient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Encounter data access.
 */
@Slf4j
public class EncounterRepository extends EntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public EncounterRepository(ReflectEntityStore entityStore) {
    super(entityStore);
  }

  /**
   * Converts patient ID and encounter ID to entity ID.
   *
   * @param patientId
   *     patient ID
   * @param encounterId
   *     encounter ID
   * @return entity ID
   */
  static EntityId toEntityId(Id<Patient> patientId, Id<Encounter> encounterId) {
    return EntityId.builder()
        .path(PatientRepository.toEntityId(patientId))
        .path(Encounter.class, encounterId)
        .build();
  }

  /**
   * Generates primary key from institution encounter identifier.
   *
   * @param encounter
   *     encounter to read property from
   * @return primary key
   */
  public static Id<Encounter> generateId(Encounter encounter) {
    return Id.of(encounter.getIdentifier());
  }

  /**
   * Saves entity.
   *
   * @param patient
   *     parent entity
   * @param encounter
   *     to save
   */
  public void save(Patient patient, Encounter encounter) {
    encounter.setId(generateId(encounter));

    entityStore.save(toEntityId(patient.getId(), encounter.getId()), encounter);
  }

  /**
   * Reads encounter.
   *
   * @param patientId
   *     parent entity ID
   * @param encounterId
   *     encounter ID
   * @return optional entity, empty if not found
   */
  public Optional<Encounter> read(Id<Patient> patientId, Id<Encounter> encounterId) {
    return entityStore.read(toEntityId(patientId, encounterId));
  }

  /**
   * Finds encounters for a patient.
   *
   * @param patientId
   *     parent entity ID
   * @return encounters
   */
  public List<Encounter> list(Id<Patient> patientId) {
    return entityStore.stream(PatientRepository.toEntityId(patientId), Encounter.class)
        .collect(Collectors.toList());
  }
}
