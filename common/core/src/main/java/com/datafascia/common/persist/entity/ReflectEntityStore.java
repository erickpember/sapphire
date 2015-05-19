// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import java.util.Optional;

/**
 * Entity persistence methods.
 */
public interface ReflectEntityStore {

  /**
   * Saves entity.
   *
   * @param entityId
   *     entity ID
   * @param object
   *     to save
   */
  void save(EntityId entityId, Object object);

  /**
   * Reads entity.
   *
   * @param entityId
   *     entity ID
   * @param <E>
   *     entity type
   * @return optional entity, empty if not found
   */
  <E> Optional<E> read(EntityId entityId);
}
