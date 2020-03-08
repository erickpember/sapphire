// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
