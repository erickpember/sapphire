// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.persist;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStoreRepository;
import com.datafascia.domain.persist.EncounterRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HarmEvidence} data access. Assumes an encounter has at most one harm evidence record.
 */
@Slf4j
public class HarmEvidenceRepository extends ReflectEntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public HarmEvidenceRepository(ReflectEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(Id<Encounter> encounterId) {
    Id<HarmEvidence> recordId = Id.of(encounterId.toString());
    return EntityId.builder()
        .path(EncounterRepository.toEntityId(encounterId))
        .path(HarmEvidence.class, recordId)
        .build();
  }

  /**
   * Saves entity.
   *
   * @param record
   *     to save
   */
  public void save(HarmEvidence record) {
    Id<Encounter> encounterId = Id.of(record.getEncounterID());
    entityStore.save(toEntityId(encounterId), record);
  }

  /**
   * Reads entity.
   *
   * @param encounterId
   *     encounter ID
   * @return optional entity, empty if not found
   */
  public Optional<HarmEvidence> read(Id<Encounter> encounterId) {
    return entityStore.read(toEntityId(encounterId));
  }

  /**
   * Deletes entity.
   *
   * @param encounterId
   *     encounter ID
   */
  public void delete(Id<Encounter> encounterId) {
    entityStore.delete(toEntityId(encounterId));
  }
}
