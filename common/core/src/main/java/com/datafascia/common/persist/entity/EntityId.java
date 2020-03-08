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

import com.datafascia.common.persist.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Unique identifier for entity.
 */
@AllArgsConstructor @Data
public class EntityId {

  /**
   * Builds entity ID.
   */
  public static class Builder {
    private final List<TypeAndId> elements = new ArrayList<>();

    /**
     * Appends path element.
     *
     * @param type
     *     entity type
     * @param id
     *     identifier
     * @return builder
     */
    public Builder path(Class<?> type, Id<?> id) {
      elements.add(new TypeAndId(type, id));
      return this;
    }

    /**
     * Appends path elements from entity ID.
     *
     * @param entityId
     *     entity ID
     * @return builder
     */
    public Builder path(EntityId entityId) {
      elements.addAll(entityId.getElements());
      return this;
    }

    /**
     * Builds entity ID.
     *
     * @return entity ID
     */
    public EntityId build() {
      return new EntityId(elements);
    }
  }

  private List<TypeAndId> elements;

  /**
   * Constructor
   *
   * @param type
   *     entity type
   * @param id
   *     identifier
   */
  public EntityId(Class<?> type, Id<?> id) {
    this(Arrays.asList(new TypeAndId(type, id)));
  }

  /**
   * Creates builder.
   *
   * @return builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Gets type of entity identified by this entity ID.
   *
   * @return entity type
   */
  public Class<?> getType() {
    return elements.get(elements.size() - 1).getType();
  }
}
