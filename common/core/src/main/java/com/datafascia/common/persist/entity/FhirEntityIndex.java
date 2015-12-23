// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import java.util.List;

/**
 * Maps search term to entity IDs.
 *
 * @param <E>
 *     entity type
 */
public interface FhirEntityIndex<E>  {

  /**
   * @return true if index does not contain any entries
   */
  boolean isEmpty();

  /**
   * Saves entity to index.
   *
   * @param entityId
   *     entity ID
   * @param oldObject
   *     old entity
   * @param newObject
   *     new entity to save
   */
  void save(EntityId entityId, E oldObject, E newObject);

  /**
   * Finds entity IDs matching search term.
   *
   * @param term
   *     search term to match
   * @return entity ID stream
   */
  List<String> search(String term);

  /**
   * Deletes entity from index.
   *
   * @param entityId
   *     entity ID
   * @param object
   *     to delete
   */
  void delete(EntityId entityId, E object);
}
