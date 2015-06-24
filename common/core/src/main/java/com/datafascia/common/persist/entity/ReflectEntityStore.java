// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import java.util.Optional;
import java.util.stream.Stream;

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

  /**
   * Reads entities into stream.
   *
   * @param parentId
   *     containing parent entity ID
   * @param entityType
   *     entity type
   * @param <E>
   *     entity type
   * @return entity stream
   */
  <E> Stream<E> stream(EntityId parentId, Class<E> entityType);

  /**
   * Reads root entities into stream.
   *
   * @param entityType
   *     entity type
   * @param <E>
   *     entity type
   * @return entity stream
   */
  <E> Stream<E> stream(Class<E> entityType);

  /**
   * Reads root entities into stream.
   *
   * @param startEntityId
   *     entity ID to start reading from
   * @param <E>
   *     entity type
   * @return entity stream
   */
  <E> Stream<E> stream(EntityId startEntityId);

  /**
   * Deletes entities and all of their children.
   *
   * @param parentId
   *     containing parent entity ID
   * @param entityType
   *     entity type
   * @param <E>
   *     entity type
   */
  <E> void delete(EntityId parentId, Class<E> entityType);
}