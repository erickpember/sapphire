// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStoreRepository;
import com.datafascia.emerge.ucsf.HarmEvidence;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HarmEvidence} data access.
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

  private static EntityId toEntityId(Id<HarmEvidence> patientId) {
    return new EntityId(HarmEvidence.class, patientId);
  }

  private static Id<HarmEvidence> generateId(HarmEvidence record) {
    return Id.of(record.getPatientID());
  }

  /**
   * Saves entity.
   *
   * @param record
   *     to save
   */
  public void save(HarmEvidence record) {
    Id<HarmEvidence> patientId = generateId(record);
    entityStore.save(toEntityId(patientId), record);
  }

  /**
   * Reads entity.
   *
   * @param patientId
   *     patient ID
   * @return optional entity, empty if not found
   */
  public Optional<HarmEvidence> read(Id<HarmEvidence> patientId) {
    return entityStore.read(toEntityId(patientId));
  }

  /**
   * Finds entities.
   *
   * @return found entities
   */
  public List<HarmEvidence> list() {
    return entityStore.stream(HarmEvidence.class)
        .collect(Collectors.toList());
  }

  /**
   * Deletes entity.
   *
   * @param patientId
   *     patient ID
   */
  public void delete(Id<HarmEvidence> patientId) {
    entityStore.delete(toEntityId(patientId));
  }
}
