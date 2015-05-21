// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.domain.model.Medication;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Medication data access.
 */
@Slf4j
public class MedicationRepository extends EntityStoreRepository {

  /**
   * Constructor
   *
   * @param entityStore
   *     entity store
   */
  @Inject
  public MedicationRepository(ReflectEntityStore entityStore) {
    super(entityStore);
  }

  private static EntityId toEntityId(Id<Medication> medicationId) {
    return new EntityId(Medication.class, medicationId);
  }

  private static Id<Medication> generateId(Medication medication) {
    return (medication.getId() != null)
        ? medication.getId()
        : Id.of(UUID.randomUUID().toString());
  }

  /**
   * Saves entity.
   *
   * @param medication
   *     to save
   */
  public void save(Medication medication) {
    medication.setId(generateId(medication));

    entityStore.save(toEntityId(medication.getId()), medication);
  }

  /**
   * Reads medication.
   *
   * @param medicationId
   *     entity ID to read
   * @return optional entity, empty if not found
   */
  public Optional<Medication> read(Id<Medication> medicationId) {
    return entityStore.read(toEntityId(medicationId));
  }
}
