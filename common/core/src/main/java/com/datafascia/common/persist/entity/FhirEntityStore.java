// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import java.util.Optional;
import java.util.stream.Stream;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Persists FHIR resources as entities.
 */
public interface FhirEntityStore {

  /**
   * Saves entity.
   *
   * @param entityId
   *     entity ID
   * @param object
   *     to save
   */
  void save(EntityId entityId, IBaseResource object);

  /**
   * Reads entity.
   *
   * @param entityId
   *     entity ID
   * @param <E>
   *     entity type
   * @return optional entity, empty if not found
   */
  <E extends IBaseResource> Optional<E> read(EntityId entityId);

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
  <E extends IBaseResource> Stream<E> stream(EntityId parentId, Class<E> entityType);

  /**
   * Reads root entities into stream.
   *
   * @param entityType
   *     entity type
   * @param <E>
   *     entity type
   * @return entity stream
   */
  <E extends IBaseResource> Stream<E> stream(Class<E> entityType);

  /**
   * Reads root entities into stream.
   *
   * @param startEntityId
   *     entity ID to start reading from
   * @param <E>
   *     entity type
   * @return entity stream
   */
  <E extends IBaseResource> Stream<E> stream(EntityId startEntityId);

  /**
   * Deletes entity and all of its children.
   *
   * @param entityId
   *     entity ID
   */
  void delete(EntityId entityId);

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
  <E extends IBaseResource> void delete(EntityId parentId, Class<E> entityType);
}
