// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
