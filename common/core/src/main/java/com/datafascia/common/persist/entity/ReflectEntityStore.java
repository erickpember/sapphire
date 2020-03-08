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
  <E> void delete(EntityId parentId, Class<E> entityType);
}
